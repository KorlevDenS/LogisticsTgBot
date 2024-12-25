create table client (
                        id serial primary key,
                        number integer not null,
                        creator integer not null,
                        name varchar not null,
                        phone varchar not null,
                        email varchar not null,
                        type varchar not null
);

create table company (
                         id serial primary key,
                         code varchar not null,
                         creator integer references client on delete cascade
);

create table ordering (
                          id serial primary key,
                          customer integer references client on delete cascade ,
                          transporter integer references client,
                          status varchar not null,
                          type varchar not null,
                          length double precision not null,
                          width double precision not null,
                          height double precision not null,
                          weight double precision not null,
                          date date not null,
                          location varchar not null
);