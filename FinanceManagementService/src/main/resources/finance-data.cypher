
CREATE (fond1:FinansijskiFond {
    idOriginal: 'FOND-001',
    naziv: 'Fond za tekuce odrzavanje',
    ukupanIznos: 450000.0,
    mesecniDoprinos: 1500.0,
    isAktivan: true
})
CREATE (fond2:FinansijskiFond {
    idOriginal: 'FOND-002',
    naziv: 'Rezervni fond',
    ukupanIznos: 820000.0,
    mesecniDoprinos: 2000.0,
    isAktivan: true
})
CREATE (fond3:FinansijskiFond {
    idOriginal: 'FOND-003',
    naziv: 'Fond za investiciono odrzavanje',
    ukupanIznos: 1200000.0,
    mesecniDoprinos: 3500.0,
    isAktivan: true
})
CREATE (fond4:FinansijskiFond {
    idOriginal: 'FOND-004',
    naziv: 'Fond za hitne intervencije',
    ukupanIznos: 150000.0,
    mesecniDoprinos: 500.0,
    isAktivan: false
})

// ============================================================
// STANOVI
// ============================================================

CREATE (stan1:Stan {
    idOriginal: 'STAN-001',
    adresa: 'Bulevar Oslobodjenja 42',
    brojStana: 1,
    kvadratura: 65.5,
    isZauzet: true
})
CREATE (stan2:Stan {
    idOriginal: 'STAN-002',
    adresa: 'Bulevar Oslobodjenja 42',
    brojStana: 2,
    kvadratura: 48.0,
    isZauzet: true
})
CREATE (stan3:Stan {
    idOriginal: 'STAN-003',
    adresa: 'Bulevar Oslobodjenja 42',
    brojStana: 3,
    kvadratura: 82.0,
    isZauzet: false
})
CREATE (stan4:Stan {
    idOriginal: 'STAN-004',
    adresa: 'Jovana Subotića 7',
    brojStana: 1,
    kvadratura: 55.0,
    isZauzet: true
})
CREATE (stan5:Stan {
    idOriginal: 'STAN-005',
    adresa: 'Jovana Subotića 7',
    brojStana: 2,
    kvadratura: 70.0,
    isZauzet: true
})
CREATE (stan6:Stan {
    idOriginal: 'STAN-006',
    adresa: 'Jovana Subotića 7',
    brojStana: 3,
    kvadratura: 90.0,
    isZauzet: true
})
CREATE (stan7:Stan {
    idOriginal: 'STAN-007',
    adresa: 'Futoska 18',
    brojStana: 1,
    kvadratura: 42.0,
    isZauzet: true
})
CREATE (stan8:Stan {
    idOriginal: 'STAN-008',
    adresa: 'Futoska 18',
    brojStana: 2,
    kvadratura: 60.0,
    isZauzet: false
})

// ============================================================
// VEZE: Stan -> FinansijskiFond (PRIPADA_FONDU)
// ============================================================

CREATE (stan1)-[:PRIPADA_FONDU]->(fond1)
CREATE (stan1)-[:PRIPADA_FONDU]->(fond2)
CREATE (stan2)-[:PRIPADA_FONDU]->(fond1)
CREATE (stan3)-[:PRIPADA_FONDU]->(fond1)
CREATE (stan3)-[:PRIPADA_FONDU]->(fond2)
CREATE (stan4)-[:PRIPADA_FONDU]->(fond2)
CREATE (stan4)-[:PRIPADA_FONDU]->(fond3)
CREATE (stan5)-[:PRIPADA_FONDU]->(fond3)
CREATE (stan6)-[:PRIPADA_FONDU]->(fond3)
CREATE (stan7)-[:PRIPADA_FONDU]->(fond1)
CREATE (stan7)-[:PRIPADA_FONDU]->(fond4)
CREATE (stan8)-[:PRIPADA_FONDU]->(fond1)

// ============================================================
// VLASNICI
// ============================================================

CREATE (v1:Vlasnik {
    idOriginal: 1,
    ime: 'Marko',
    prezime: 'Petrović',
    email: 'marko.petrovic@email.com',
    telefon: '064/111-2222',
    isAktivan: true
})
CREATE (v2:Vlasnik {
    idOriginal: 2,
    ime: 'Ana',
    prezime: 'Jovanović',
    email: 'ana.jovanovic@email.com',
    telefon: '063/333-4444',
    isAktivan: true
})
CREATE (v3:Vlasnik {
    idOriginal: 3,
    ime: 'Nikola',
    prezime: 'Stojanović',
    email: 'nikola.stojanovic@email.com',
    telefon: '065/555-6666',
    isAktivan: true
})
CREATE (v4:Vlasnik {
    idOriginal: 4,
    ime: 'Jelena',
    prezime: 'Marković',
    email: 'jelena.markovic@email.com',
    telefon: '060/777-8888',
    isAktivan: true
})
CREATE (v5:Vlasnik {
    idOriginal: 5,
    ime: 'Petar',
    prezime: 'Nikolić',
    email: 'petar.nikolic@email.com',
    telefon: '061/999-0000',
    isAktivan: false
})

// ============================================================
// VEZE: Vlasnik -> Stan (POSEDUJE)
// ============================================================

CREATE (v1)-[:POSEDUJE]->(stan1)
CREATE (v1)-[:POSEDUJE]->(stan2)
CREATE (v2)-[:POSEDUJE]->(stan3)
CREATE (v2)-[:POSEDUJE]->(stan4)
CREATE (v3)-[:POSEDUJE]->(stan5)
CREATE (v3)-[:POSEDUJE]->(stan6)
CREATE (v4)-[:POSEDUJE]->(stan7)
CREATE (v5)-[:POSEDUJE]->(stan8)

// ============================================================
// RACUNI
// ============================================================

CREATE (r1:Racun {
    idOriginal: 'RAC-001',
    tip: 'kirija',
    iznos: 35000.0,
    datumIzdavanja: '2026-03-01',
    rokPlacanja: '2026-03-15',
    isPlacen: true
})
CREATE (r2:Racun {
    idOriginal: 'RAC-002',
    tip: 'komunalije',
    iznos: 8500.0,
    datumIzdavanja: '2026-03-01',
    rokPlacanja: '2026-03-20',
    isPlacen: true
})
CREATE (r3:Racun {
    idOriginal: 'RAC-003',
    tip: 'kirija',
    iznos: 28000.0,
    datumIzdavanja: '2026-03-01',
    rokPlacanja: '2026-03-15',
    isPlacen: false
})
CREATE (r4:Racun {
    idOriginal: 'RAC-004',
    tip: 'struja',
    iznos: 4200.0,
    datumIzdavanja: '2026-03-05',
    rokPlacanja: '2026-03-25',
    isPlacen: false
})
CREATE (r5:Racun {
    idOriginal: 'RAC-005',
    tip: 'kirija',
    iznos: 42000.0,
    datumIzdavanja: '2026-04-01',
    rokPlacanja: '2026-04-15',
    isPlacen: false
})
CREATE (r6:Racun {
    idOriginal: 'RAC-006',
    tip: 'komunalije',
    iznos: 9200.0,
    datumIzdavanja: '2026-04-01',
    rokPlacanja: '2026-04-20',
    isPlacen: false
})
CREATE (r7:Racun {
    idOriginal: 'RAC-007',
    tip: 'kirija',
    iznos: 32000.0,
    datumIzdavanja: '2026-04-01',
    rokPlacanja: '2026-04-15',
    isPlacen: true
})
CREATE (r8:Racun {
    idOriginal: 'RAC-008',
    tip: 'struja',
    iznos: 5100.0,
    datumIzdavanja: '2026-04-05',
    rokPlacanja: '2026-04-25',
    isPlacen: false
})
CREATE (r9:Racun {
    idOriginal: 'RAC-009',
    tip: 'komunalije',
    iznos: 7800.0,
    datumIzdavanja: '2026-04-01',
    rokPlacanja: '2026-04-20',
    isPlacen: true
})
CREATE (r10:Racun {
    idOriginal: 'RAC-010',
    tip: 'kirija',
    iznos: 25000.0,
    datumIzdavanja: '2026-03-01',
    rokPlacanja: '2026-03-15',
    isPlacen: false
})
CREATE (r11:Racun {
    idOriginal: 'RAC-011',
    tip: 'kirija',
    iznos: 38000.0,
    datumIzdavanja: '2026-04-01',
    rokPlacanja: '2026-04-15',
    isPlacen: false
})
CREATE (r12:Racun {
    idOriginal: 'RAC-012',
    tip: 'struja',
    iznos: 3800.0,
    datumIzdavanja: '2026-04-05',
    rokPlacanja: '2026-04-25',
    isPlacen: true
})

// ============================================================
// STANARI
// ============================================================

CREATE (st1:Stanar {
    idOriginal: 1,
    ime: 'Milan',
    prezime: 'Đorđević',
    email: 'milan.djordjevic@email.com',
    telefon: '064/123-4567',
    isAktivan: true
})
CREATE (st2:Stanar {
    idOriginal: 2,
    ime: 'Maja',
    prezime: 'Simić',
    email: 'maja.simic@email.com',
    telefon: '063/234-5678',
    isAktivan: true
})
CREATE (st3:Stanar {
    idOriginal: 3,
    ime: 'Stefan',
    prezime: 'Pavlović',
    email: 'stefan.pavlovic@email.com',
    telefon: '065/345-6789',
    isAktivan: true
})
CREATE (st4:Stanar {
    idOriginal: 4,
    ime: 'Ivana',
    prezime: 'Ilić',
    email: 'ivana.ilic@email.com',
    telefon: '060/456-7890',
    isAktivan: true
})
CREATE (st5:Stanar {
    idOriginal: 5,
    ime: 'Aleksandar',
    prezime: 'Vasić',
    email: 'aleksandar.vasic@email.com',
    telefon: '061/567-8901',
    isAktivan: true
})
CREATE (st6:Stanar {
    idOriginal: 6,
    ime: 'Tamara',
    prezime: 'Kostić',
    email: 'tamara.kostic@email.com',
    telefon: '069/678-9012',
    isAktivan: true
})

// ============================================================
// VEZE: Stanar -> Stan (STANUJE_U)
// ============================================================

CREATE (st1)-[:STANUJE_U {datumUseljenja: '2024-01-15', mesecnaKirija: 35000.0}]->(stan1)
CREATE (st2)-[:STANUJE_U {datumUseljenja: '2023-06-01', mesecnaKirija: 28000.0}]->(stan2)
CREATE (st3)-[:STANUJE_U {datumUseljenja: '2025-03-01', mesecnaKirija: 42000.0}]->(stan5)
CREATE (st4)-[:STANUJE_U {datumUseljenja: '2024-09-01', mesecnaKirija: 32000.0}]->(stan4)
CREATE (st5)-[:STANUJE_U {datumUseljenja: '2022-11-15', mesecnaKirija: 25000.0}]->(stan7)
CREATE (st6)-[:STANUJE_U {datumUseljenja: '2025-01-01', mesecnaKirija: 38000.0}]->(stan6)

// ============================================================
// VEZE: Stanar -> Racun (IMA_RACUN)
// ============================================================

// Milan Đorđević - stan1
CREATE (st1)-[:IMA_RACUN {datumPlacanja: '2026-03-10', isNaVreme: true}]->(r1)
CREATE (st1)-[:IMA_RACUN {datumPlacanja: '2026-03-18', isNaVreme: true}]->(r2)
CREATE (st1)-[:IMA_RACUN {datumPlacanja: null, isNaVreme: false}]->(r5)
CREATE (st1)-[:IMA_RACUN {datumPlacanja: null, isNaVreme: false}]->(r6)

// Maja Simić - stan2
CREATE (st2)-[:IMA_RACUN {datumPlacanja: null, isNaVreme: false}]->(r3)
CREATE (st2)-[:IMA_RACUN {datumPlacanja: null, isNaVreme: false}]->(r4)

// Stefan Pavlović - stan5
CREATE (st3)-[:IMA_RACUN {datumPlacanja: null, isNaVreme: false}]->(r8)
CREATE (st3)-[:IMA_RACUN {datumPlacanja: null, isNaVreme: false}]->(r11)

// Ivana Ilić - stan4
CREATE (st4)-[:IMA_RACUN {datumPlacanja: '2026-04-12', isNaVreme: true}]->(r7)
CREATE (st4)-[:IMA_RACUN {datumPlacanja: '2026-04-18', isNaVreme: true}]->(r9)

// Aleksandar Vasić - stan7
CREATE (st5)-[:IMA_RACUN {datumPlacanja: null, isNaVreme: false}]->(r10)

// Tamara Kostić - stan6
CREATE (st6)-[:IMA_RACUN {datumPlacanja: '2026-04-20', isNaVreme: false}]->(r12)

RETURN "Finance baza je popunjena: 5 vlasnika, 8 stanova, 6 stanara, 12 racuna, 4 fonda";
