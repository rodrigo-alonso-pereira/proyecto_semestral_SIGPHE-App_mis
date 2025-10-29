# Usa la imagen oficial de Nginx basada en Alpine Linux
FROM nginx:alpine
# Copia los archivos estáticos generados por la aplicación al directorio predeterminado de Nginx
COPY dist /usr/share/nginx/html
# Expone el puerto 80 para el tráfico HTTP
EXPOSE 80
# Comando para iniciar Nginx en primer plano (CMD y ENTRYPOINT son equivalentes)
CMD ["nginx", "-g", "daemon off;"]