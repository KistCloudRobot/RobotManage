package net.ion.cloudrobot.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * AWS S3 Utils
 */
@Service
@RequiredArgsConstructor
public class AwsS3Utils {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    private final AmazonS3Client s3;


    /**
     * @param folderName
     * @param multipartFile
     * @return
     * @throws IOException
     */
    public String uploadFile(String folderName, MultipartFile multipartFile) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(()->new IllegalArgumentException("MultipartFile 형식을 File로 전환하는 데에 실패하였습니다."));
        return upload(uploadFile, folderName);
    }

    /**
     * @param uploadFile
     * @param folderName
     * @return String uploadImageUrl
     */
    private String upload(File uploadFile, String folderName){
        String fileName = folderName + "/" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    /**
     * @param uploadFile
     * @param fileName
     * @return String S3 file url
     */
    private String putS3(File uploadFile, String fileName){
        s3.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return s3.getUrl(bucket, fileName).toString();
    }

    /**
     * @param targetFile
     */
    private void removeNewFile(File targetFile){
        if(targetFile.delete()){
            System.out.println("파일이 삭제되었습니다.");
        }else{
            System.out.println("파일 삭제가 실패되었습니다.");
        }
    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.currentTimeMillis() + StringUtils.cleanPath(file.getOriginalFilename()));

        if(convertFile.createNewFile()){
            try(FileOutputStream fos = new FileOutputStream(convertFile)){
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    /**
     * @param link
     * @return
     * @throws URISyntaxException
     */
    public InputStreamResource download(String link) throws URISyntaxException {
        URI fileLink = new URI(link);
        AmazonS3URI s3URI = new AmazonS3URI(fileLink);

        S3Object s3Object = s3.getObject(s3URI.getBucket(), s3URI.getKey());
        S3ObjectInputStream oi = s3Object.getObjectContent();

        return new InputStreamResource(oi);
    }


}
