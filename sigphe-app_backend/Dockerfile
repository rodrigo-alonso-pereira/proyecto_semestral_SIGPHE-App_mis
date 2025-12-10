# Usa una imagen base ligera de Eclipse Temurin con JRE 21 (java requerido por el proyecto)
FROM eclipse-temurin:21-jre-alpine
# Configurar zona horaria
ENV TZ=America/Santiago
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone && \
    apk del tzdata
# Establece el directorio de trabajo dentro del contenedor
ARG JAR_FILE=target/sigphe-app_backend-v2.1.3.jar
# Copia el archivo JAR construido desde el host al contenedor
COPY ${JAR_FILE} sigphe-app_backend-v2.1.3.jar
# Punto de entrada para ejecutar la aplicación Java cuando se inicie el contenedor
ENTRYPOINT ["java","-jar","/sigphe-app_backend-v2.1.3.jar"]