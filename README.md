# Tienda de Barrio

Aplicación web desarrollada con Spring Boot para gestionar una tienda local, usuarios, ventas y administración.

## Requisitos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- Java 17 o superior
- Maven 3.9+ o usar el wrapper incluido (`mvnw` / `mvnw.cmd`)
- MySQL 8 o superior
- Git (opcional, para clonar y versionar)

## Clonar y entrar al proyecto

```bash
git clone <url-del-repositorio>
cd Tienda_de_Barrio
```

## Configuración de la base de datos

1. Inicia MySQL.
2. Crea una base de datos llamada `tienda_barrio`.

```sql
CREATE DATABASE tienda_barrio;
```

3. Asegúrate de que tu usuario tenga permisos para crear, leer, actualizar y eliminar registros en esa base de datos.

## Variables de entorno

La aplicación usa variables de entorno para conectarse a MySQL y para crear el primer administrador al iniciar.

### Windows PowerShell

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/tienda_barrio"
$env:DB_USER="root"
$env:DB_PASSWORD="tu_password"
$env:ADMIN_NOMBRE="Admin"
$env:ADMIN_CORREO="admin@correo.com"
$env:ADMIN_CONTRASENA="TuPassword123"
```

### Linux/macOS

```bash
export DB_URL="jdbc:mysql://localhost:3306/tienda_barrio"
export DB_USER="root"
export DB_PASSWORD="tu_password"
export ADMIN_NOMBRE="Admin"
export ADMIN_CORREO="admin@correo.com"
export ADMIN_CONTRASENA="TuPassword123"
```

> Importante: usa una contraseña segura y almacénala de forma segura. El primer administrador se crea al iniciar la aplicación si las variables `ADMIN_*` están configuradas.

## Ejecutar el proyecto en modo desarrollo

Desde la raíz del proyecto:

```bash
./mvnw spring-boot:run
```

En Windows:

```powershell
./mvnw.cmd spring-boot:run
```

La aplicación quedará disponible en:

```text
http://localhost:8080
```

## Compilar el proyecto

```bash
./mvnw clean package
```

Y luego ejecutar el JAR generado:

```bash
java -jar target/Tienda_de_Barrio-0.0.1-SNAPSHOT.jar
```

## Estructura principal

```text
src/
├── main/
│   ├── java/       # Código Java / Spring Boot
│   └── resources/  # application.properties, plantillas, archivos estáticos
└── test/           # Pruebas
```

## Solución de problemas comunes

### Error de conexión a MySQL
- Verifica que MySQL esté corriendo.
- Revisa que la base de datos `tienda_barrio` exista.
- Confirma que `DB_URL`, `DB_USER` y `DB_PASSWORD` sean correctos.

### Error de autenticación o inicio
- Verifica que `ADMIN_NOMBRE`, `ADMIN_CORREO` y `ADMIN_CONTRASENA` estén definidos antes de iniciar.
- La contraseña debe tener al menos 8 caracteres.

### Puerto ocupado
Si el puerto `8080` está en uso, puedes cambiarlo en `src/main/resources/application.properties`:

```properties
server.port=8081
```

## Notas adicionales

- Se recomienda no dejar las variables de bootstrap `ADMIN_*` activas en producción una vez que el primer administrador ya fue creado.
- La aplicación usa Spring Security y Thymeleaf.
- Si necesitas más información sobre endpoints o módulos, revisa el código en `src/main/java`.
