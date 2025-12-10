# Usa una imagen base ligera de Eclipse Temurin con JRE 21 (java requerido por el proyecto)
FROM eclipse-temurin:21-jre-alpine
# Establece el directorio de trabajo dentro del contenedor
ARG JAR_FILE=target/sigphe-app_backend-v2.1.2.jar
# Copia el archivo JAR construido desde el host al contenedor
COPY ${JAR_FILE} sigphe-app_backend-v2.1.2.jar
# Punto de entrada para ejecutar la aplicación Java cuando se inicie el contenedor
ENTRYPOINT ["java","-jar","/sigphe-app_backend-v2.1.2.jar"]