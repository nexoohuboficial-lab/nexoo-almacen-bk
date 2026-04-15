# Usamos una imagen oficial y ligera de Java 21
FROM eclipse-temurin:21-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo .jar de tu proyecto compilado al contenedor.
# (Asegúrate de compilar tu proyecto antes, por ejemplo con ./gradlew build)
# Cambia 'mi-app.jar' por el nombre real que genera tu proyecto.
COPY app/build/libs/*SNAPSHOT.jar app.jar

# Exponemos el puerto (asumiendo que tu app Java usa el 8080)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]