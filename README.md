# Backend de Device Control
## Observaciones:
- EnableAsync para enviar los correos de forma asíncrona y no bloquear el registro
- Mostrar mensaje de que si no llega código de PasswordReset, contactar con soporte (Si no llega posiblemente el correo no existe)
- Manejar la expiración del token, refresh
- La ruta de register debe estar protegida, ya puedo crear usuarios con la app en 0 gracias a AdminInitializer
- Archivo util para getCurrentUser()?

## Conexión con la base de datos

- admindevice
- &wt6*YQ6<6'x

- hostname=spring-devicecontrol.mysql.database.azure.com
port=3306
username=admindevice
password={your-password}
ssl-mode=require