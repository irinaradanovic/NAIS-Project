CREATE
(d1:Dostavljac {
  korisnikid: 1,
  ime: "Marko",
  prez: "Markovic",
  telefon: "061123456",
  lozinka: "pass",
  email: "marko@gmail.com",
  datumregistracije: date("2024-01-10"),
  trenutni_status: "slobodan",
  brojdostava: 15,
  prosecnovreme: 25
}),

(d2:Dostavljac {
  korisnikid: 2,
  ime: "Ana",
  prez: "Anic",
  telefon: "062222333",
  lozinka: "pass",
  email: "ana@gmail.com",
  datumregistracije: date("2024-02-15"),
  trenutni_status: "zauzet",
  brojdostava: 30,
  prosecnovreme: 20
}),

(k1:Kupac {
  korisnikid: 10,
  ime: "Nikola",
  prez: "Nikolic",
  telefon: "063111222",
  lozinka: "pass",
  email: "nikola@gmail.com",
  datumregistracije: date("2023-12-01")
}),

(k2:Kupac {
  korisnikid: 11,
  ime: "Jovana",
  prez: "Jovanovic",
  telefon: "064333444",
  lozinka: "pass",
  email: "jovana@gmail.com",
  datumregistracije: date("2023-11-20")
}),

(r1:Restoran {
  restoranid: 100,
  menadzerid: 500,
  naziv: "Pizza Place",
  adresa: "Bulevar Oslobodjenja 10",
  kontakt: "021123456"
}),

(r2:Restoran {
  restoranid: 101,
  menadzerid: 501,
  naziv: "Burger House",
  adresa: "Cara Dusana 5",
  kontakt: "021654321"
}),

(p1:Porudzbina {
  porudzbinaid: 1000,
  datumkreiranja: datetime("2025-04-28T12:00:00"),
  status: "u toku",
  tipporudzbine: "dostava"
}),

(p2:Porudzbina {
  porudzbinaid: 1001,
  datumkreiranja: datetime("2025-04-28T13:00:00"),
  status: "isporuceno",
  tipporudzbine: "dostava"
});

MATCH (d:Dostavljac {korisnikid:1}), (p:Porudzbina {porudzbinaid:1000})
CREATE (d)-[:DOSTAVLJA]->(p);

MATCH (d:Dostavljac {korisnikid:2}), (p:Porudzbina {porudzbinaid:1001})
CREATE (d)-[:DOSTAVLJA]->(p);

MATCH (p:Porudzbina {porudzbinaid:1000}), (r:Restoran {restoranid:100})
CREATE (p)-[:PREUZIMA_IZ]->(r);

MATCH (p:Porudzbina {porudzbinaid:1001}), (r:Restoran {restoranid:101})
CREATE (p)-[:PREUZIMA_IZ]->(r);

MATCH (p:Porudzbina {porudzbinaid:1000}), (k:Kupac {korisnikid:10})
CREATE (p)-[:ISPORUCUJE]->(k);

MATCH (p:Porudzbina {porudzbinaid:1001}), (k:Kupac {korisnikid:11})
CREATE (p)-[:ISPORUCUJE]->(k);

CREATE (d:Dostavljac {
  korisnikid:3, ime:"Petar", prez:"Petrovic",
  telefon:"065111222", lozinka:"pass",
  email:"petar@gmail.com",
  datumregistracije:date(),
  trenutni_status:"slobodan",
  brojdostava:0,
  prosecnovreme:0
});

MATCH (d:Dostavljac)
RETURN d.ime, d.prez, d.trenutni_status;

MATCH (d:Dostavljac {korisnikid:1})
SET d.trenutni_status = "zauzet",
    d.brojdostava = d.brojdostava + 1
RETURN d;

MATCH (d:Dostavljac {korisnikid:3})
DELETE d;

MATCH (d:Dostavljac)-[:DOSTAVLJA]->(p:Porudzbina)
WITH d, COUNT(p) AS broj
WHERE broj > 0
RETURN d.ime, d.prez, broj
ORDER BY broj DESC;

MATCH (d:Dostavljac)
WITH d, d.prosecnovreme AS avgVreme
WHERE avgVreme > 0
RETURN d.ime, avgVreme
ORDER BY avgVreme ASC;

MATCH (p:Porudzbina)-[:PREUZIMA_IZ]->(r:Restoran)
WITH r, COUNT(p) AS broj
WHERE broj > 0
RETURN r.naziv, broj
ORDER BY broj DESC;

MATCH (d:Dostavljac {korisnikid:1})-[:DOSTAVLJA]->(p:Porudzbina)
SET p.status = "isporuceno"
RETURN p;

MATCH (d:Dostavljac {korisnikid:2}), (p:Porudzbina {porudzbinaid:1000})
MERGE (d)-[:DOSTAVLJA]->(p)
RETURN d, p;