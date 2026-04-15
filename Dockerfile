# ================================================================
# Multi-stage build: compila el JAR dentro de Docker
# Etapa 1: BUILD
# ================================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copiamos solo los archivos de configuración de Gradle primero
# para aprovechar la caché de capas de Docker
COPY gradlew gradlew.bat gradle.properties settings.gradle.kts ./
COPY gradle/ gradle/
RUN chmod +x gradlew

# Descargamos dependencias antes de copiar el código fuente (caché)
COPY app/build.gradle.kts app/build.gradle.kts
RUN ./gradlew :app:dependencies --no-daemon 2>/dev/null || true

# Ahora copiamos el código fuente completo y compilamos
COPY app/ app/
RUN ./gradlew :app:bootJar --no-daemon -x test

# ================================================================
# Etapa 2: RUNTIME — imagen final ligera
# ================================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos únicamente el JAR generado en la etapa de build
COPY --from=builder /build/app/build/libs/*SNAPSHOT.jar app.jar

# Exponemos el puerto de Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]