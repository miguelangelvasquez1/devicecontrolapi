# Backend de Device Control
## Observaciones:
- EnableAsync para enviar los correos de forma asíncrona y no bloquear el registro
- Mostrar mensaje de que si no llega código de PasswordReset, contactar con soporte (Si no llega posiblemente el correo no existe)
- Manejar la expiración del token, refresh
- La ruta de register debe estar protegida