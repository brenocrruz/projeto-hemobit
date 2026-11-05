# Nginx da camada pública com iproute2 (tc), usado pelo benchmarking para degradar o enlace
# Nginx -> API com netem (perda, atraso e jitter reais na camada de rede).
FROM nginx:1.27-alpine
RUN apk add --no-cache iproute2-tc
