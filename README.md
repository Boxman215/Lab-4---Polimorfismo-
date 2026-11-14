# Lab 4 - Polimorfismo

Aplicacion de consola en Java para administrar torneos de ArenaGT.

## Requisitos

- Java JDK 8 o superior.
- Consola de comandos.

## Compilar

Desde la carpeta del proyecto:

```text
javac -encoding UTF-8 -d bin src\*.java
```

## Ejecutar la aplicacion

```text
java -cp bin App
```

## Ejecutar las pruebas

```text
java -cp bin Pruebas
```

El resultado esperado es:

```text
Pruebas aprobadas: 33
```

## Funciones incluidas

- Registro de participantes y torneos.
- Cotizacion e inscripcion.
- Futbol sala.
- Futbol sala con penales.
- Baloncesto.
- Voleibol.
- Relevos.
- Cierre de inscripciones.
- Generacion y programacion de eventos.
- Reprogramacion con validacion de conflictos.
- Registro de resultados.
- Tablas de posiciones.
- Desempates por diferencia, victorias y nombre.
- Finalizacion automatica y calculo de premios.
- Calendarios general, por torneo y por participante.
- Reportes de ingresos, premios y ranking de empresas.

## Organizacion principal

- `Disciplina` y `ReglasDisciplina` contienen las reglas polimorficas.
- `DisciplinaPartido` concentra el comportamiento comun de los encuentros.
- `FutbolSala`, `FutbolSalaPenales`, `Baloncesto` y `Voleibol` implementan reglas de partidos.
- `Relevos` implementa las reglas de carreras.
- `Evento` es la clase abstracta para `Encuentro` y `CarreraRelevos`.
- `Torneo` administra participantes, eventos, estados y tabla.
- `Sistema` centraliza las operaciones y reportes.
- `Consola` contiene el menu.
- `Pruebas` ejecuta las pruebas automaticas.

Los datos iniciales se cargan directamente en memoria y no se utilizan archivos ni base de datos.
