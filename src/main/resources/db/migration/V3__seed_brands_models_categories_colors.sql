
CREATE TABLE IF NOT EXISTS brands (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS colors (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    hex_code VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS models (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    brand_id BIGINT NOT NULL REFERENCES brands(id) ON DELETE CASCADE
);

INSERT INTO brands (name)
SELECT v.brand_name
FROM (VALUES
    ('Audi'),
    ('BMW'),
    ('Mercedes-Benz'),
    ('Volkswagen'),
    ('Toyota'),
    ('Volvo'),
    ('Skoda'),
    ('Ford'),
    ('Kia'),
    ('Hyundai'),
    ('Nissan'),
    ('Honda'),
    ('Mazda'),
    ('Opel'),
    ('Peugeot'),
    ('Renault'),
    ('Citroen'),
    ('Fiat'),
    ('Seat'),
    ('Cupra'),
    ('Porsche'),
    ('Lexus'),
    ('Subaru'),
    ('Mitsubishi'),
    ('Suzuki'),
    ('Jeep'),
    ('Land Rover'),
    ('Jaguar'),
    ('Tesla'),
    ('Alfa Romeo'),
    ('Dacia'),
    ('Chevrolet'),
    ('Dodge'),
    ('Chrysler'),
    ('Mini'),
    ('Genesis')
) AS v(brand_name)
WHERE NOT EXISTS (
    SELECT 1 FROM brands b WHERE b.name = v.brand_name
);

INSERT INTO categories (name)
SELECT v.category_name
FROM (VALUES
    ('Osobowe'),
    ('SUV'),
    ('Hatchback'),
    ('Sedan'),
    ('Kombi'),
    ('Crossover'),
    ('Dostawcze'),
    ('Coupe')
) AS v(category_name)
WHERE NOT EXISTS (
    SELECT 1 FROM categories c WHERE c.name = v.category_name
);

INSERT INTO colors (name, hex_code)
SELECT v.color_name, v.hex_code
FROM (VALUES
    ('White', '#FFFFFF'),
    ('Black', '#000000'),
    ('Gray', '#808080'),
    ('Silver', '#C0C0C0'),
    ('Red', '#E53935'),
    ('Blue', '#1E88E5'),
    ('Green', '#43A047'),
    ('Beige', '#F5F5DC'),
    ('Brown', '#8D6E63'),
    ('Yellow', '#FDD835'),
    ('Orange', '#FB8C00'),
    ('Purple', '#8E24AA')
) AS v(color_name, hex_code)
WHERE NOT EXISTS (
    SELECT 1 FROM colors c WHERE c.name = v.color_name
);

WITH brand_data(brand_name, models) AS (
    VALUES
        ('Audi', ARRAY['A1', 'A3', 'A4', 'A6', 'Q3', 'Q5']::text[]),
        ('BMW', ARRAY['1 Series', '3 Series', '5 Series', 'X1', 'X3', 'X5']::text[]),
        ('Mercedes-Benz', ARRAY['A-Class', 'C-Class', 'E-Class', 'GLA', 'GLC', 'GLS']::text[]),
        ('Volkswagen', ARRAY['Polo', 'Golf', 'Passat', 'Tiguan', 'T-Roc', 'ID.4']::text[]),
        ('Toyota', ARRAY['Yaris', 'Corolla', 'C-HR', 'RAV4', 'Camry', 'Land Cruiser']::text[]),
        ('Volvo', ARRAY['XC40', 'XC60', 'XC90', 'S60', 'S90', 'V60']::text[]),
        ('Skoda', ARRAY['Fabia', 'Scala', 'Octavia', 'Superb', 'Karoq', 'Kodiaq']::text[]),
        ('Ford', ARRAY['Fiesta', 'Focus', 'Mondeo', 'Puma', 'Kuga', 'Explorer']::text[]),
        ('Kia', ARRAY['Picanto', 'Ceed', 'Sportage', 'Niro', 'EV6', 'Sorento']::text[]),
        ('Hyundai', ARRAY['i20', 'i30', 'Tucson', 'Santa Fe', 'Kona', 'IONIQ 5']::text[]),
        ('Nissan', ARRAY['Micra', 'Qashqai', 'Juke', 'X-Trail', 'Leaf', 'Navara']::text[]),
        ('Honda', ARRAY['Jazz', 'Civic', 'HR-V', 'CR-V', 'Accord', 'Type R']::text[]),
        ('Mazda', ARRAY['Mazda2', 'Mazda3', 'Mazda6', 'CX-30', 'CX-5', 'CX-60']::text[]),
        ('Opel', ARRAY['Corsa', 'Astra', 'Insignia', 'Mokka', 'Grandland', 'Combo']::text[]),
        ('Peugeot', ARRAY['208', '308', '508', '2008', '3008', '5008']::text[]),
        ('Renault', ARRAY['Clio', 'Megane', 'Arkana', 'Captur', 'Kadjar', 'Austral']::text[]),
        ('Citroen', ARRAY['C3', 'C4', 'C5 Aircross', 'Berlingo', 'C4 X', 'Jumpy']::text[]),
        ('Fiat', ARRAY['Panda', '500', 'Tipo', 'Doblo', '500X', 'Scudo']::text[]),
        ('Seat', ARRAY['Ibiza', 'Leon', 'Arona', 'Ateca', 'Tarraco', 'Toledo']::text[]),
        ('Cupra', ARRAY['Born', 'Formentor', 'Leon', 'Ateca', 'Tavascan', 'Terramar']::text[]),
        ('Porsche', ARRAY['911', 'Cayenne', 'Macan', 'Panamera', 'Taycan', '718 Cayman']::text[]),
        ('Lexus', ARRAY['LBX', 'UX', 'NX', 'RX', 'ES', 'LS']::text[]),
        ('Subaru', ARRAY['Impreza', 'Outback', 'Forester', 'Crosstrek', 'WRX', 'BRZ']::text[]),
        ('Mitsubishi', ARRAY['Space Star', 'ASX', 'Eclipse Cross', 'Outlander', 'L200', 'Pajero Sport']::text[]),
        ('Suzuki', ARRAY['Swift', 'Vitara', 'S-Cross', 'Jimny', 'Ignis', 'Across']::text[]),
        ('Jeep', ARRAY['Renegade', 'Compass', 'Grand Cherokee', 'Wrangler', 'Avenger', 'Gladiator']::text[]),
        ('Land Rover', ARRAY['Defender', 'Discovery', 'Range Rover Evoque', 'Range Rover Velar', 'Range Rover Sport', 'Range Rover']::text[]),
        ('Jaguar', ARRAY['XE', 'XF', 'F-Pace', 'E-Pace', 'I-Pace', 'F-Type']::text[]),
        ('Tesla', ARRAY['Model 3', 'Model Y', 'Model S', 'Model X', 'Cybertruck', 'Roadster']::text[]),
        ('Alfa Romeo', ARRAY['Giulia', 'Stelvio', 'Tonale', 'Junior', 'Giulietta', '4C']::text[]),
        ('Dacia', ARRAY['Sandero', 'Duster', 'Jogger', 'Spring', 'Logan', 'Bigster']::text[]),
        ('Chevrolet', ARRAY['Spark', 'Onix', 'Tracker', 'Equinox', 'Traverse', 'Silverado']::text[]),
        ('Dodge', ARRAY['Challenger', 'Charger', 'Durango', 'Journey', 'Hornet', 'Ram 1500']::text[]),
        ('Chrysler', ARRAY['300', 'Pacifica', 'Voyager', '300C', 'Aspen', 'Crossfire']::text[]),
        ('Mini', ARRAY['Cooper', 'Countryman', 'Aceman', 'Clubman', 'Paceman', 'Cabrio']::text[]),
        ('Genesis', ARRAY['G70', 'G80', 'G90', 'GV60', 'GV70', 'GV80']::text[])
)
INSERT INTO models (name, brand_id)
SELECT u.model_name, b.id
FROM brand_data bd
JOIN brands b ON b.name = bd.brand_name
CROSS JOIN LATERAL unnest(bd.models) AS u(model_name)
WHERE NOT EXISTS (
    SELECT 1 FROM models m
    WHERE m.brand_id = b.id AND m.name = u.model_name
);



