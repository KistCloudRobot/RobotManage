docker build -t fuel-cell-cart .
docker tag fuel-cell-cart:latest 686532090731.dkr.ecr.ap-northeast-2.amazonaws.com/fuel-cell-cart:latest

aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 686532090731.dkr.ecr.ap-northeast-2.amazonaws.com
docker push 686532090731.dkr.ecr.ap-northeast-2.amazonaws.com/fuel-cell-cart:latest