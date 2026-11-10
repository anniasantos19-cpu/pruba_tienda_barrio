# Tienda de Barrio

Aplicación web para administrar las operaciones de una tienda de barrio. Incluye
vistas web renderizadas con Thymeleaf y controladores REST para trabajar con
productos, clientes, proveedores, compras, ventas y usuarios.

Esta documentación corresponde al estado del proyecto actualizado el **7 de
noviembre de 2026**.

## Funcionalidades

- Inicio de sesión y control de acceso mediante Spring Security.
- Administración de usuarios y roles; las operaciones administrativas están
  restringidas a los roles `ADMIN` y `ADMINISTRADOR`.
- Gestión de productos, clientes y proveedores.
- Registro de compras y ventas, con sus respectivos detalles.
- Registro de ventas fiadas y consulta de su estado de pago.
- Almacenamiento y visualización de fecha y hora para compras y ventas.
- Creación opcional del primer administrador a partir de variables de entorno.
- Actualización de contraseñas heredadas en texto plano a BCrypt cuando el
  usuario inicia sesión correctamente.

## Tecnologías

- Java 17
- Spring Boot 4.1.2
- Spring MVC y controladores REST
- Thymeleaf
- Spring Security
- Spring JDBC
- MySQL Connector/J
- Maven 3.10.0 mediante Maven Wrapper

## Requisitos

- JDK 17 o posterior.
- MySQL 8 o posterior.
- Git para clonar el repositorio.
- No es necesario instalar Maven: el repositorio incluye `mvnw` y `mvnw.cmd`.

## Obtener el código

```bash
git clone <url-del-repositorio>
cd Tienda_de_Barrio
```

En Windows, los comandos Maven siguientes se ejecutan con `.\mvnw.cmd`. En Linux
o macOS se ejecutan con `./mvnw`.

## Configuración

### Base de datos

Para una instalación nueva, ejecuta el script
[`src/main/resources/db/schema.sql`](src/main/resources/db/schema.sql) con el
cliente de MySQL. Crea la base de datos `tienda_barrio` y sus tablas, incluyendo
los campos actuales de fecha/hora y ventas fiadas:

```powershell
cmd /c "mysql -u root -p < src\main\resources\db\schema.sql"
```

El comando solicitará la contraseña del usuario MySQL. Si tu cliente no está
en el `PATH`, abre el archivo y ejecuta su contenido desde MySQL Workbench u
otra herramienta compatible. Ajusta el usuario según tu instalación.

Configura un usuario MySQL con permisos para consultar y modificar las tablas
de la aplicación. La URL, el usuario y la contraseña se configuran con
`DB_URL`, `DB_USER` y `DB_PASSWORD`. Si no se especifican, la URL predeterminada
es `jdbc:mysql://localhost:3306/tienda_barrio` y el usuario predeterminado es
`root`.

### Actualizaciones del esquema

Las migraciones incluidas son scripts SQL y **no se ejecutan automáticamente**
al arrancar. Son para actualizar bases de datos existentes que todavía no
tienen esos cambios. No las ejecutes después de crear una base de datos nueva
con `schema.sql`, porque ese esquema ya incluye dichos cambios.

1. `src/main/resources/db/migrations/V2__estado_fiado_ventas.sql`: agrega a
   `Ventas` los campos `es_fiado` y `pagado`. Las ventas existentes se consideran
   de contado y pagadas. Si existen ventas fiadas previas, corrige sus valores
   después de ejecutar el script.
2. `src/main/resources/db/migrations/V3__fecha_hora_operaciones.sql`: cambia a
   `DATETIME` la fecha de `Ventas` y `Compras`. En registros antiguos la hora no
   estaba almacenada y se mostrará como `00:00:00`.

Ejecuta cada archivo una sola vez. Por ejemplo, desde el cliente de MySQL:

```sql
USE tienda_barrio;
SOURCE C:/ruta/al/proyecto/src/main/resources/db/migrations/V2__estado_fiado_ventas.sql;
SOURCE C:/ruta/al/proyecto/src/main/resources/db/migrations/V3__fecha_hora_operaciones.sql;
```

Reemplaza `C:/ruta/al/proyecto` por la ruta absoluta local. También puedes
ejecutar el contenido de cada archivo con tu herramienta habitual de MySQL.

### Variables de entorno

Configura los valores antes de iniciar la aplicación. Las variables
`ADMIN_*` son opcionales, pero si se define una de ellas deben definirse las
tres.

#### Windows PowerShell

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/tienda_barrio"
$env:DB_USER = "root"
$env:DB_PASSWORD = "tu_password_mysql"
$env:ADMIN_NOMBRE = "Administrador"
$env:ADMIN_CORREO = "admin@correo.com"
$env:ADMIN_CONTRASENA = "una_contrasena_segura"
```

#### Linux o macOS

```bash
export DB_URL="jdbc:mysql://localhost:3306/tienda_barrio"
export DB_USER="root"
export DB_PASSWORD="tu_password_mysql"
export ADMIN_NOMBRE="Administrador"
export ADMIN_CORREO="admin@correo.com"
export ADMIN_CONTRASENA="una_contrasena_segura"
```

El administrador inicial se crea solo si las tres variables `ADMIN_*` tienen
valor, la contraseña tiene al menos ocho caracteres y el correo aún no existe.
Si el correo ya está registrado, esa cuenta no se modifica. Al terminar el
bootstrap, elimina las variables `ADMIN_*` del entorno y nunca guardes
contraseñas reales en el repositorio.

## Ejecutar en desarrollo

Desde la raíz del proyecto:

```bash
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Abre [http://localhost:8080](http://localhost:8080). La aplicación requiere
conexión a MySQL y un usuario existente; si configuras el bootstrap, podrás
iniciar sesión con la cuenta creada en ese arranque.

## Compilar, probar y ejecutar

Ejecuta las pruebas:

```bash
./mvnw test
```

En Windows:

```powershell
.\mvnw.cmd test
```

Empaqueta la aplicación:

```bash
./mvnw clean package
```

El JAR ejecutable se genera en `target/Tienda_de_Barrio-0.0.1-SNAPSHOT.jar`:

```bash
java -jar target/Tienda_de_Barrio-0.0.1-SNAPSHOT.jar
```

## Generar el Javadoc

El Javadoc se genera localmente dentro del proyecto; los archivos generados
quedan en `target/reports/apidocs/` y no se editan manualmente:

```bash
./mvnw javadoc:javadoc
```

En Windows:

```powershell
.\mvnw.cmd javadoc:javadoc
```

Abre `target/reports/apidocs/index.html` en un navegador para consultar la
documentación de los paquetes y tipos Java.

## Estructura del proyecto

```text
src/
├── main/
│   ├── java/com/Tienda/Tienda_de_Barrio/
│   │   ├── config/                 # Seguridad, autenticación y admin inicial
│   │   ├── controller/
│   │   │   ├── mvc/                # Rutas web y vistas Thymeleaf
│   │   │   └── res/                # API REST
│   │   ├── Modelo/
│   │   │   ├── DAO/                # Acceso JDBC a datos
│   │   │   └── VO/                 # Objetos de transferencia de datos
│   │   ├── service/                # Operaciones de negocio compartidas
│   │   └── TiendaDeBarrioApplication.java
│   └── resources/
│       ├── db/schema.sql           # Esquema completo para instalaciones nuevas
│       ├── db/migrations/          # Scripts SQL para actualizar bases existentes
│       ├── static/                 # CSS, JavaScript e imágenes
│       └── templates/              # Vistas HTML Thymeleaf
└── test/
    └── java/                       # Pruebas de contexto y controladores
```

### Interfaces web y API

Las vistas web se sirven desde rutas como `/web/productos`, `/web/clientes`,
`/web/proveedores`, `/web/compras`, `/web/ventas` y `/web/usuarios`. La pantalla
de inicio de sesión es `/login`.

La API REST expone recursos bajo `/api`, incluidos:

| Recurso | Ruta base |
|---|---|
| Productos | `/api/productos` |
| Clientes | `/api/clientes` |
| Proveedores | `/api/proveedores` |
| Compras y detalles | `/api/compras`, `/api/detalles-compras` |
| Ventas y detalles | `/api/ventas`, `/api/detalles-ventas` |
| Usuarios | `/api/usuarios` |

Las operaciones protegidas requieren iniciar sesión. La administración de
usuarios y las operaciones de escritura restringidas requieren el rol
`ADMIN` o `ADMINISTRADOR`. Para conocer los métodos HTTP y los datos aceptados,
consulta los controladores REST en `src/main/java/com/Tienda/Tienda_de_Barrio/controller/res`.

## Configuración adicional

- La aplicación usa el puerto `8080` de forma predeterminada. Para cambiarlo,
  establece `SERVER_PORT` en el entorno o ajusta `server.port` en
  `src/main/resources/application.properties`.
- Las contraseñas nuevas se almacenan con BCrypt. Las contraseñas heredadas en
  texto plano se actualizan a BCrypt después de una autenticación exitosa.
- Spring Security mantiene habilitada la protección CSRF predeterminada; usa el
  formulario de inicio de sesión y las vistas de la aplicación en vez de
  deshabilitarla para resolver errores de solicitudes POST.

## Solución de problemas

### No se puede conectar a MySQL

- Confirma que el servidor MySQL esté iniciado y accesible.
- Verifica que `tienda_barrio` exista y que `DB_URL`, `DB_USER` y `DB_PASSWORD`
  correspondan a tu entorno.
- Revisa los permisos del usuario y que se hayan aplicado las migraciones
  requeridas.

### La aplicación no inicia con variables `ADMIN_*`

- Define `ADMIN_NOMBRE`, `ADMIN_CORREO` y `ADMIN_CONTRASENA` juntas, o elimina
  las tres para omitir la creación inicial.
- Usa una contraseña de al menos ocho caracteres.
- Si el correo ya existe, el bootstrap no cambia esa cuenta.

### Error al iniciar sesión

- Confirma que el usuario exista y que sus credenciales sean correctas.
- Si la cuenta es nueva, verifica que el bootstrap se haya configurado antes
  del inicio de la aplicación.
- Los usuarios necesitan un rol asignado para autenticarse correctamente.

### Puerto ocupado

Configura otro puerto, por ejemplo, en PowerShell:

```powershell
$env:SERVER_PORT = "8081"
.\mvnw.cmd spring-boot:run
```

## Documentación de referencia

- `HELP.md`: notas operativas del proyecto y cambios de esquema.
- `src/main/resources/db/migrations/`: scripts SQL versionados.
- `target/reports/apidocs/index.html`: Javadoc generado localmente.
- `src/test/java/`: pruebas automatizadas.
