ALTER TABLE pets DROP CONSTRAINT ck_pets_species;

ALTER TABLE pets
    ADD CONSTRAINT ck_pets_species CHECK (
        species IN (
                    'DOG',
                    'CAT',
                    'BIRD',
                    'RABBIT',
                    'HAMSTER',
                    'GUINEA_PIG',
                    'FERRET',
                    'REPTILE',
                    'SNAKE',
                    'LIZARD',
                    'TURTLE',
                    'FISH',
                    'HORSE',
                    'COW',
                    'PIG',
                    'OTHER'
            )
        );
