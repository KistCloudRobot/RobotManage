cd frontend
npm install
npm run build
cd ..
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 686532090731.dkr.ecr.ap-northeast-2.amazonaws.com
./mvnw compile jib:build
