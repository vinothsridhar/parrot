## Parrot Chat bot
Side project to learn AI embedding and prompt engineering. 
It uses spring boot AI, pgvector for storing embedding and OpenAI for prompting.

![](/docs/screenshots/2.png)
![](/docs/screenshots/1.png)

### Build
`./gradlew build`

> make sure you have `OPENAI_API_KEY` in the environment variable to pass tests

### To run locally using jar:

#### Prerequisite
> - parrot.jar
> - pgvector database


- Download latest jar from the release section
- You need to have below environment variables ready
    - `DS_HOST` (pgvector server hostname/ip)
    - `DS_USERNAME` (pgvector username)
    - `DS_PASSWORD` (pgvector password)
    - `DS_DBNAME` (pgvector database name)
    - `OPENAI_API_KEY` (openai api key)
- Run `java -jar parrot.jar`
- App will be running on `http://localhost:8080`

### To run locally using docker-compose:

```dockerfile
version: '3.3'

services:
  postgres:
    image: pgvector/pgvector:pg16
    restart: always
    container_name: pgvector
    hostname: pgvector
    ports:
      - "5432:5432"
    volumes:
      - db:/data
    environment:
      - POSTGRES_PASSWORD=test1234
      - POSTGRES_USER=postgres
      - POSTGRES_DB=postgres
    networks:
      - parrotnet
  parrot:
    image: vinothsridhar/parrot
    restart: always
    container_name: parrot
    hostname: parrot
    ports:
      - "8080:8080"
    volumes:
      - parrot:/data
    networks:
      - parrotnet
    environment:
      DS_HOST: pgvector
      DS_USERNAME: postgres
      DS_PASSWORD: test1234
      DS_DBNAME: postgres
      OPENAI_API_KEY: ${OPENAI_API_KEY}
    depends_on:
      - postgres
volumes:
  db:
    driver: local
  parrot:
    driver: local
networks:
  parrotnet:
    driver: bridge
```