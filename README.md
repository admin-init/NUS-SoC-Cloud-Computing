# NUS-SoC-Cloud-Computing
This repository is for ***GROUP PROJECT*** of NUS-SoC-Cloud-Computing-Course using.


# Kubenetes 

## Back-end deployment
refering to [Back-end deployment](./kubernetes/back-end/README.md)

## Front-end deployment

refering to [Front-end deployment](./kubernetes/front-end/README.md)


# Docker compose

The back-end support basic run through docker compose.
```bash
cd docker-compose
docker compose up
```

Then you can access the back-end API through:
[openapi swagger-ui webpage](http://localhost:8080/q/swagger-ui/), you may change the port to `50080` and `50081`

# Build from source

You can try build the back-end refering to [Back-end build](./ticket-system/README.md)