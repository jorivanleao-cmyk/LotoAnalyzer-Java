CREATE TABLE IF NOT EXISTS concursos (
    id BIGSERIAL PRIMARY KEY,
    numero_concurso INT NOT NULL UNIQUE,
    data_sorteio DATE NOT NULL,
    n1 INT NOT NULL,
    n2 INT NOT NULL,
    n3 INT NOT NULL,
    n4 INT NOT NULL,
    n5 INT NOT NULL,
    n6 INT NOT NULL,
    n7 INT NOT NULL,
    n8 INT NOT NULL,
    n9 INT NOT NULL,
    n10 INT NOT NULL,
    n11 INT NOT NULL,
    n12 INT NOT NULL,
    n13 INT NOT NULL,
    n14 INT NOT NULL,
    n15 INT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_concursos_data ON concursos (data_sorteio);
CREATE INDEX IF NOT EXISTS idx_concursos_numero ON concursos (numero_concurso);
