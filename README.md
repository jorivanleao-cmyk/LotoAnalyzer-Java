# LotoAnalyzer-Java

Sistema inteligente para análise estatística e geração de jogos da Lotofácil com Java 21, Spring Boot 3, JavaFX, PostgreSQL e algoritmos probabilísticos.

## Objetivo

O projeto foi estruturado para um cenário de TCC, com backend analítico e frontend desktop para consumo das informações estatísticas.

## Funcionalidades

### Módulo 1 - Importação de Resultados

- Importa histórico da Lotofácil por URL CSV (fonte oficial configurável).
- Evita duplicidade de concursos já existentes.
- Compatível com PostgreSQL, MySQL e SQLite via profiles.

### Módulo 2 - Estatísticas

- Números mais sorteados.
- Números menos sorteados.
- Frequência por ano.
- Frequência por mês.
- Frequência por concurso.

### Módulo 3 - Análise de Tendências

- Números quentes e frios (janela dos últimos 100 concursos).
- Números atrasados.
- Repetição do último concurso.
- Pares x ímpares.
- Soma das dezenas (min, max e média).

### Módulo 4 - Inteligência Estatística

Score por número com base em:

$$
score =
frequenciaHistorica \times 0.40 +
frequenciaUltimos100 \times 0.30 +
atraso \times 0.20 +
repeticaoPadrao \times 0.10
$$

### Módulo 5 - Gerador de Jogos

- Top 5 jogos recomendados.
- Top 10 jogos alternativos.
- Jogos de 15 dezenas com score agregado.

### Módulo 6 - Machine Learning (Serviço Separado)

- Endpoint dedicado para Random Forest (surrogate).
- Endpoint dedicado para XGBoost (surrogate).
- Endpoint dedicado para Rede Neural (surrogate).
- Retorno com top 15 dezenas e score por número.

### Módulo 7 - Endpoints de Gráficos (JFreeChart-ready)

- Série anual por número.
- Série mensal por número.
- Pacote de séries anuais para top 15 dezenas sugeridas.

## Estrutura do Repositório

```text
LotoAnalyzer-Java/
├── README.md
├── pom.xml
├── backend/
│   ├── pom.xml
│   └── src/
├── frontend/
│   ├── pom.xml
│   └── src/
├── database/
│   ├── scripts.sql
│   └── scripts/
├── dataset/
│   └── lotofacil.csv
├── docs/
└── tests/
```

## Arquitetura de Backend

Pacotes principais:

- model: Concurso, Jogo
- repository: ConcursoRepository
- service: EstatisticaService, ProbabilidadeService, GeradorJogosService, TendenciaService, ImportacaoService
- controller: MainController

## Endpoints

Base: /api/loto

- POST /importar
- GET /estatisticas/mais-sorteados
- GET /estatisticas/menos-sorteados
- GET /estatisticas/frequencia-ano
- GET /estatisticas/frequencia-mes
- GET /estatisticas/frequencia-concurso
- GET /tendencias
- GET /score
- GET /jogos
- GET /ml/random-forest
- GET /ml/xgboost
- GET /ml/rede-neural
- GET /graficos/anual/{numero}
- GET /graficos/mensal/{numero}
- GET /graficos/pacote-anual-top15

### Exemplo de retorno para gráficos

```json
{
	"titulo": "Frequencia anual - numero 10",
	"pontos": [
		{ "label": "2023", "valor": 118 },
		{ "label": "2024", "valor": 121 }
	]
}
```

### Exemplo de retorno ML

```json
{
	"algoritmo": "xgboost-surrogate",
	"top15": [10, 20, 25, 3, 6, 11, 14, 19, 21, 7, 8, 12, 1, 4, 16],
	"scorePorNumero": {
		"10": 0.9231,
		"20": 0.9122
	}
}
```

## Banco de Dados

Scripts disponíveis:

- PostgreSQL: database/scripts.sql
- MySQL: database/scripts/mysql.sql
- SQLite: database/scripts/sqlite.sql

### Tabela principal

```sql
CREATE TABLE concursos (
	id SERIAL PRIMARY KEY,
	numero_concurso INT,
	data_sorteio DATE,
	n1 INT, n2 INT, n3 INT, n4 INT, n5 INT,
	n6 INT, n7 INT, n8 INT, n9 INT, n10 INT,
	n11 INT, n12 INT, n13 INT, n14 INT, n15 INT
);
```

## Como executar

Pré-requisitos:

- Java 21
- Maven 3.9+
- PostgreSQL, MySQL ou SQLite

### 1. Backend

```bash
cd backend
mvn spring-boot:run
```

Profiles:

- padrão: PostgreSQL
- mysql: mvn spring-boot:run -Dspring-boot.run.profiles=mysql
- sqlite: mvn spring-boot:run -Dspring-boot.run.profiles=sqlite

### 2. Frontend JavaFX

```bash
cd frontend
mvn javafx:run
```

### 3. Subir tudo com Docker Compose

```bash
docker compose up -d --build
```

Serviços:

- PostgreSQL: localhost:5432
- Backend: localhost:8080

Para derrubar:

```bash
docker compose down
```

## Diferencial para TCC

O projeto está preparado para extensão com Machine Learning:

- Random Forest
- XGBoost
- Redes neurais

Usando aproximadamente 10 anos de concursos públicos para treinamento e validação.
