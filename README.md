
# 1. Evidencias de pruebas funcionales
## 1.1 Organizador modifica un evento ajeno
Esta prueba permite comprobar que un organizador no puede modificar un evento perteneciente a otro organizador.

Se inició sesión con María Cordero y se intentó actualizar el evento número 2, perteneciente a José Mora.

<img src="assets/1.png" width="600">

## 1.2 Inscripción duplicada
Esta prueba comprueba que un participante no pueda registrarse dos veces en el mismo evento.

Se intentó crear una nueva inscripción para un participante que ya estaba registrado previamente.

<img src="assets/2.png" width="600">

## 1.4. Inscripción en evento finalizado
Esta prueba comprueba que el sistema no permita nuevas inscripciones en eventos que ya finalizaron.

Se intentó registrar un participante en el evento número 6, cuyo estado era FINISHED

<img src="assets/4.png" width="600">

## 1.5. Inscripción en evento no publicado

Esta prueba verifica que las inscripciones únicamente se permitan en eventos con estado PUBLISHED.

Se intentó registrar un participante en el evento número 4, cuyo estado era DRAFT.

<img src="assets/5.png" width="600">

## 1.6. Evento sin cupos disponibles

Esta prueba permite comprobar que el sistema no permita registrar más participantes cuando la capacidad disponible del evento llega a cero.

Primero se creó un evento con capacidad de una persona. Después se creó y confirmó una inscripción, dejando el evento sin cupos disponibles.

Finalmente, se intentó registrar un segundo participante.

### Evidencia 1: evento sin cupos
<img src="assets/6.png" width="600">

### Evidencia 2: nueva inscripción rechazada
<img src="assets/6_1.png" width="600">

## 1.7. Transacción de inscripción y disponibilidad

Esta prueba comprueba que la disponibilidad del evento se actualice correctamente al cambiar el estado de una inscripción.

Al confirmar una inscripción, la capacidad disponible disminuyó en una unidad. Posteriormente, al cancelar la inscripción, el cupo fue restaurado.

### Evidencia 1: capacidad inicial
<img src="assets/7.png" width="600">

### Evidencia 2: inscripción confirmada
<img src="assets/7_1.png" width="600">


### Evidencia 3: capacidad disminuida
<img src="assets/7_2.png" width="600">


### Evidencia 4: capacidad restaurada
<img src="assets/7_3.png" width="600">

## 1.8. Eliminación de evento con inscripciones

Esta prueba verifica que un evento con inscripciones asociadas no pueda ser eliminado.
Se intentó eliminar el evento número 1, el cual ya tenía participantes registrados.

<img src="assets/8.png" width="600">

## 1.9. Estadísticas por rango de fechas

Esta prueba permite comprobar la generación de estadísticas de eventos e inscripciones dentro de un rango de fechas.

### Evidencia 1: rango válido
<img src="assets/9.png" width="600">

### Evidencia 2: rango inválido

También se probaron fechas invertidas, colocando startDate después de endDate.

<img src="assets/9_1.png" width="600">

## 1.10. Reporte PDF de inscritos

Esta prueba verifica la generación del reporte de participantes inscritos en formato PDF.

La propietaria del evento solicitó el reporte correctamente.

### Evidencia 1: respuesta del endpoint y visualización del archivo
<img src="assets/10_1.png" width="600">

### Evidencia 2: acceso denegado
Intento realizado por un organizador ajeno
<img src="assets/10.png" width="600">

## 1.11. Reporte Excel de inscritos
Esta prueba verifica la generación del reporte de inscritos en formato Excel.

<img src="assets/11.png" width="600">
<img src="assets/11_1.png" width="600">

## 1.12. Certificado PDF

Esta prueba permite comprobar la generación segura de certificados de participación.

Primero, el participante propietario de una inscripción confirmada solicitó su certificado.

### Evidencia 1: certificado generado y visualización
<img src="assets/12.png" width="600">

### Evidencia 3: participante diferente
<img src="assets/12_1.png" width="600">

### Evidencia 4: inscripción pendiente
<img src="assets/12_2.png" width="600">

## 1.13. Pruebas automatizadas

Finalmente, se ejecutaron las pruebas automatizadas del proyecto mediante Gradle.

El reporte generado **mostró**:
17 tests
0 failures
0 skipped
100% successful
<img src="assets/13.png" width="600">
