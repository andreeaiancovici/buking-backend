CREATE TABLE property
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255),
    address     VARCHAR(255),
    description TEXT,
    star_rating INTEGER,
    image_url   VARCHAR(1000)
);

INSERT INTO property (name, address, description, star_rating, image_url)
VALUES ('Hilton Downtown', '123 Main Street, New York', 'Luxury hotel in the heart of Manhattan', 5,
        'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb'),
       ('Seaside Resort', '456 Beach Road, Miami', 'Beautiful beachfront property with ocean views', 4,
        'https://images.unsplash.com/photo-1571896349842-33c89424de2d'),
       ('Mountain Lodge', '789 Pine Avenue, Denver', 'Cozy mountain retreat with spectacular views', 4,
        'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4');

CREATE TABLE room
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_number     VARCHAR(50),
    room_type       VARCHAR(50),
    price_per_night DECIMAL(10, 2),
    capacity        INTEGER,
    image_url       VARCHAR(1000),
    property_id     BIGINT,
    FOREIGN KEY (property_id) REFERENCES property (id)
);

INSERT INTO room (room_number, room_type, price_per_night, capacity, property_id, image_url)
VALUES ('101', 'STANDARD', 199.99, 2, 1, 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304'),
       ('102', 'DELUXE', 299.99, 2, 1, 'https://images.unsplash.com/photo-1618773928121-c32242e63f39'),
       ('201', 'SUITE', 499.99, 4, 1, 'https://images.unsplash.com/photo-1590490360182-c33d57733427'),

       ('A101', 'OCEAN VIEW', 259.99, 2, 2, 'https://images.unsplash.com/photo-1582719508461-905c673771fd'),
       ('A102', 'BEACH FRONT', 359.99, 3, 2, 'https://images.unsplash.com/photo-1566665797739-1674de7a421a'),
       ('B201', 'FAMILY SUITE', 459.99, 5, 2, 'https://images.unsplash.com/photo-1616594039964-ae9021a400a0'),

       ('L1', 'CABIN', 199.99, 2, 3, 'https://images.unsplash.com/photo-1602002418082-a4443e081dd1'),
       ('L2', 'LUXURY CABIN', 299.99, 4, 3, 'https://images.unsplash.com/photo-1598928506311-c55ded91a20c'),
       ('L3', 'MOUNTAIN SUITE', 399.99, 6, 3, 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461');

CREATE TABLE users
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR2(50),
    name VARCHAR2(50)
);

INSERT INTO users ( email, name)
VALUES  ('ana.popescu@example.com', 'Ana Popescu'),
        ('ion.ionescu@example.com', 'Ion Ionescu'),
        ('maria.stan@example.com', 'Maria Stan'),
        ('andrei.george@example.com', 'Andrei George'),
        ('elena.dumitru@example.com', 'Elena Dumitru');

CREATE TABLE bookings
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    check_in VARCHAR2(20),
    check_out VARCHAR2(20),
    user_id BIGINT,
    property_id BIGINT,
    room_id BIGINT,
    price float,
    FOREIGN KEY (user_id) references users(id),
    FOREIGN KEY (property_id) REFERENCES property (id),
    FOREIGN KEY (room_id) references room(id)

);

insert into bookings( check_in, check_out, user_id, property_id, room_id,price)
values ('2025-10-05', '2025-10-10', 1, 1, 1,250),  -- Ana Popescu la Hilton, camera 101
       ('2025-11-01', '2025-11-05', 2, 1, 2,500),  -- Ion Ionescu la Hilton, camera 102
       ('2025-12-20', '2025-12-27', 3, 2, 4,300),  -- Maria Stan la Seaside, camera A101
       ('2026-01-15', '2026-01-18', 4, 2, 5,290),  -- Andrei George la Seaside, camera A102
       ('2026-02-10', '2026-02-14', 5, 2, 6,890),  -- Elena Dumitru la Seaside, camera B201
       ('2026-03-01', '2026-03-07', 1, 3, 7,400),  -- Ana Popescu la Mountain Lodge, cabina L1
       ('2026-04-12', '2026-04-15', 2, 3, 8,350),  -- Ion Ionescu la Mountain Lodge, cabina L2
       ('2026-05-20', '2026-05-25', 3, 3, 9,500);  -- Maria Stan la Mountain Lodge, Mountain Suite