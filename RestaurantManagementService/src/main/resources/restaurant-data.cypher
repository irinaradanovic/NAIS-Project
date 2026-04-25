// PRVI RESTORAN - GONDOLA

CREATE (gondola:Restaurant {
    name: 'Gondola Novi Sad',
    address: 'Bulevar Mihajla Pupina 18',
    contact: '021/123-4567'
})

// Meniji
CREATE (gondola_m1:Menu {
    menuId: 101, version: 1, name: 'Standardni Meni',
    description: 'Osnovna ponuda restorana', type: 'STANDARD',
    activationDate: date('2026-01-01'), deactivationDate: date('2026-03-01')
})
CREATE (gondola_m2:Menu {
    menuId: 101, version: 2, name: 'Standardni Meni Proleće',
    description: 'Osvežena ponuda za proleće', type: 'STANDARD',
    activationDate: date('2026-03-01')
})
CREATE (gondola_mU:Menu {
    menuId: 202, version: 1, name: 'Uskršnji Specijal',
    description: 'Sezonska ponuda za Uskrs', type: 'SEASONAL',
    startSeasonDate: date('2026-04-10'), endSeasonDate: date('2026-04-20'),
    activationDate: date('2026-04-10')
})

CREATE (gondola)-[:HAS_MENU {active: false}]->(gondola_m1)
CREATE (gondola)-[:HAS_MENU {active: true}]->(gondola_m2)
CREATE (gondola)-[:HAS_MENU {active: true}]->(gondola_mU)

// Kategorije - stara verzija (m1)
CREATE (gondola_m1_cat1:Category {name: 'Pizze'})
CREATE (gondola_m1_cat2:Category {name: 'Paste'})
CREATE (gondola_m1_cat3:Category {name: 'Deserti'})

CREATE (gondola_m1)-[:HAS_CATEGORY {order: 1}]->(gondola_m1_cat1)
CREATE (gondola_m1)-[:HAS_CATEGORY {order: 2}]->(gondola_m1_cat2)
CREATE (gondola_m1)-[:HAS_CATEGORY {order: 3}]->(gondola_m1_cat3)

// Items - stara verzija
CREATE (gondola_m1_i1:MenuItem {
    name: 'Margarita', price: 850.0, calories: 800,
    description: 'Pelat, mocarela, bosiljak', quantity: 350, unit: 'g',
    timeMin: 10, timeMax: 15
})
CREATE (gondola_m1_i2:MenuItem {
    name: 'Carbonara', price: 1100.0, calories: 1200,
    description: 'Slanina, jaja, parmezan', quantity: 400, unit: 'g',
    timeMin: 15, timeMax: 20
})
CREATE (gondola_m1_i3:MenuItem {
    name: 'Tiramisu', price: 400.0, calories: 500,
    description: 'Kafa, maskarpone, piskote', quantity: 150, unit: 'g',
    timeMin: 5, timeMax: 5
})

CREATE (gondola_m1_cat1)-[:INCLUDES_ITEM {discount: 0.0}]->(gondola_m1_i1)
CREATE (gondola_m1_cat2)-[:INCLUDES_ITEM {discount: 0.0}]->(gondola_m1_i2)
CREATE (gondola_m1_cat3)-[:INCLUDES_ITEM {discount: 0.0}]->(gondola_m1_i3)

// Kategorije - nova verzija (m2)
CREATE (gondola_m2_cat1:Category {name: 'Pizze'})
CREATE (gondola_m2_cat2:Category {name: 'Paste'})
CREATE (gondola_m2_cat3:Category {name: 'Deserti'})
CREATE (gondola_m2_cat4:Category {name: 'Salate'})

CREATE (gondola_m2)-[:HAS_CATEGORY {order: 1}]->(gondola_m2_cat1)
CREATE (gondola_m2)-[:HAS_CATEGORY {order: 2}]->(gondola_m2_cat2)
CREATE (gondola_m2)-[:HAS_CATEGORY {order: 3}]->(gondola_m2_cat3)
CREATE (gondola_m2)-[:HAS_CATEGORY {order: 4}]->(gondola_m2_cat4)

// Items - nova verzija (vise stavki, vise cene)
CREATE (gondola_m2_i1:MenuItem {
    name: 'Margarita', price: 950.0, calories: 800,
    description: 'Pelat, mocarela, svezi bosiljak', quantity: 350, unit: 'g',
    timeMin: 10, timeMax: 15
})
CREATE (gondola_m2_i2:MenuItem {
    name: 'Quattro Formaggi', price: 1150.0, calories: 950,
    description: 'Cetiri vrste sira', quantity: 380, unit: 'g',
    timeMin: 10, timeMax: 15
})
CREATE (gondola_m2_i3:MenuItem {
    name: 'Carbonara', price: 1200.0, calories: 1200,
    description: 'Slanina, jaja, parmezan', quantity: 400, unit: 'g',
    timeMin: 15, timeMax: 20
})
CREATE (gondola_m2_i4:MenuItem {
    name: 'Penne Arrabiata', price: 1050.0, calories: 900,
    description: 'Ljuta paradajz salsa, beli luk', quantity: 380, unit: 'g',
    timeMin: 12, timeMax: 18
})
CREATE (gondola_m2_i5:MenuItem {
    name: 'Tiramisu', price: 450.0, calories: 500,
    description: 'Kafa, maskarpone, piskote', quantity: 150, unit: 'g',
    timeMin: 5, timeMax: 5
})
CREATE (gondola_m2_i6:MenuItem {
    name: 'Panna Cotta', price: 380.0, calories: 420,
    description: 'Krem desert sa karamelom', quantity: 130, unit: 'g',
    timeMin: 3, timeMax: 5
})
CREATE (gondola_m2_i7:MenuItem {
    name: 'Cezar Salata', price: 850.0, calories: 420,
    description: 'Romaine zelena salata, parmezan, krutoni', quantity: 300, unit: 'g',
    timeMin: 7, timeMax: 10
})

CREATE (gondola_m2_cat1)-[:INCLUDES_ITEM {discount: 0.1, discountFrom: date('2026-04-01'), discountTo: date('2026-05-01')}]->(gondola_m2_i1)
CREATE (gondola_m2_cat1)-[:INCLUDES_ITEM {discount: 0.0}]->(gondola_m2_i2)
CREATE (gondola_m2_cat2)-[:INCLUDES_ITEM {discount: 0.0}]->(gondola_m2_i3)
CREATE (gondola_m2_cat2)-[:INCLUDES_ITEM {discount: 0.0}]->(gondola_m2_i4)
CREATE (gondola_m2_cat3)-[:INCLUDES_ITEM {discount: 0.2, discountFrom: date('2026-03-01'), discountTo: date('2026-06-01')}]->(gondola_m2_i5)
CREATE (gondola_m2_cat3)-[:INCLUDES_ITEM {discount: 0.0}]->(gondola_m2_i6)
CREATE (gondola_m2_cat4)-[:INCLUDES_ITEM {discount: 0.0}]->(gondola_m2_i7)

// Kategorije i items - Uskrsnji specijal
CREATE (gondola_mU_cat1:Category {name: 'Uskrsnja Jela'})
CREATE (gondola_mU_cat2:Category {name: 'Uskrsnji Deserti'})

CREATE (gondola_mU)-[:HAS_CATEGORY {order: 1}]->(gondola_mU_cat1)
CREATE (gondola_mU)-[:HAS_CATEGORY {order: 2}]->(gondola_mU_cat2)

CREATE (gondola_mU_i1:MenuItem {
    name: 'Jagnjetina sa ruzmarinom', price: 2200.0, calories: 1400,
    description: 'Pecena jagnjetina sa povrcem', quantity: 500, unit: 'g',
    timeMin: 40, timeMax: 60
})
CREATE (gondola_mU_i2:MenuItem {
    name: 'Uskrsnja Pogaca', price: 350.0, calories: 320,
    description: 'Domaca pogaca sa sirom', quantity: 200, unit: 'g',
    timeMin: 5, timeMax: 8
})
CREATE (gondola_mU_i3:MenuItem {
    name: 'Cokoladni Kolac', price: 480.0, calories: 580,
    description: 'Topli cokoladni kolac sa sladoledom', quantity: 180, unit: 'g',
    timeMin: 8, timeMax: 12
})

CREATE (gondola_mU_cat1)-[:INCLUDES_ITEM {discount: 0.0}]->(gondola_mU_i1)
CREATE (gondola_mU_cat1)-[:INCLUDES_ITEM {discount: 0.0}]->(gondola_mU_i2)
CREATE (gondola_mU_cat2)-[:INCLUDES_ITEM {discount: 0.15}]->(gondola_mU_i3)

// DRUGI RESTORAN - KFC

CREATE (r2:Restaurant {
    name: 'KFC Novi Sad',
    address: 'Bulevar Oslobođenja 119',
    contact: '021/222-3033'
})

CREATE (kfc_m1:Menu {
    menuId: 303, version: 1, name: 'Klasicni Meni',
    description: 'KFC osnovna ponuda', type: 'STANDARD',
    activationDate: date('2025-02-03'), deactivationDate: date('2025-07-01')
})
CREATE (kfc_m2:Menu {
    menuId: 303, version: 2, name: 'Klasicni Meni',
    description: 'KFC osnovna ponuda - letnja revizija', type: 'STANDARD',
    activationDate: date('2025-07-01'), deactivationDate: date('2026-01-01')
})
CREATE (kfc_m3:Menu {
    menuId: 303, version: 3, name: 'Klasicni Meni',
    description: 'KFC ponuda 2026', type: 'STANDARD',
    activationDate: date('2026-01-01')
})
CREATE (kfc_m4:Menu {
    menuId: 404, version: 1, name: 'Sladoled Meni',
    description: 'KFC osvezenje za prolece i leto', type: 'SEASONAL',
    startSeasonDate: date('2026-03-01'), endSeasonDate: date('2026-10-01'),
    activationDate: date('2026-03-01')
})
CREATE (kfc_m5:Menu {
    menuId: 505, version: 1, name: 'Dorucak Meni',
    description: 'KFC Dorucak', type: 'TIME',
    startTime: time('08:00:00'), endTime: time('12:00:00'),
    activationDate: date('2025-05-01')
})

CREATE (kfc)-[:HAS_MENU {active: false}]->(kfc_m1)
CREATE (kfc)-[:HAS_MENU {active: false}]->(kfc_m2)
CREATE (kfc)-[:HAS_MENU {active: true}]->(kfc_m3)
CREATE (kfc)-[:HAS_MENU {active: true}]->(kfc_m4)
CREATE (kfc)-[:HAS_MENU {active: true}]->(kfc_m5)

CREATE (kfc_m1_cat1:Category {name: 'Kofice'})
CREATE (kfc_m1_cat2:Category {name: 'Burgeri'})

CREATE (kfc_m1)-[:HAS_CATEGORY {order: 1}]->(kfc_m1_cat1)
CREATE (kfc_m1)-[:HAS_CATEGORY {order: 2}]->(kfc_m1_cat2)

CREATE (kfc_m1_i1:MenuItem {
    name: 'Original Recipe Kofice 3kom', price: 750.0, calories: 620,
    description: 'Originalni KFC recept', quantity: 3, unit: 'kom',
    timeMin: 8, timeMax: 12
})
CREATE (kfc_m1_i2:MenuItem {
    name: 'Zinger Burger', price: 650.0, calories: 540,
    description: 'Ljuti piletina burger', quantity: 1, unit: 'kom',
    timeMin: 5, timeMax: 8
})

CREATE (kfc_m1_cat1)-[:INCLUDES_ITEM {discount: 0.0}]->(kfc_m1_i1)
CREATE (kfc_m1_cat2)-[:INCLUDES_ITEM {discount: 0.0}]->(kfc_m1_i2)

// Kategorije i items - verzija 2
CREATE (kfc_m2_cat1:Category {name: 'Kofice'})
CREATE (kfc_m2_cat2:Category {name: 'Burgeri'})
CREATE (kfc_m2_cat3:Category {name: 'Wrapperi'})

CREATE (kfc_m2)-[:HAS_CATEGORY {order: 1}]->(kfc_m2_cat1)
CREATE (kfc_m2)-[:HAS_CATEGORY {order: 2}]->(kfc_m2_cat2)
CREATE (kfc_m2)-[:HAS_CATEGORY {order: 3}]->(kfc_m2_cat3)

CREATE (kfc_m2_i1:MenuItem {
    name: 'Original Recipe Kofice 3kom', price: 780.0, calories: 620,
    description: 'Originalni KFC recept', quantity: 3, unit: 'kom',
    timeMin: 8, timeMax: 12
})
CREATE (kfc_m2_i2:MenuItem {
    name: 'Zinger Burger', price: 680.0, calories: 540,
    description: 'Ljuti piletina burger', quantity: 1, unit: 'kom',
    timeMin: 5, timeMax: 8
})
CREATE (kfc_m2_i3:MenuItem {
    name: 'Twister Wrap', price: 720.0, calories: 490,
    description: 'Piletina, salata, majonez u tortilja', quantity: 1, unit: 'kom',
    timeMin: 5, timeMax: 8
})

CREATE (kfc_m2_cat1)-[:INCLUDES_ITEM {discount: 0.0}]->(kfc_m2_i1)
CREATE (kfc_m2_cat2)-[:INCLUDES_ITEM {discount: 0.0}]->(kfc_m2_i2)
CREATE (kfc_m2_cat3)-[:INCLUDES_ITEM {discount: 0.0}]->(kfc_m2_i3)

// Kategorije i items - verzija 3 (trenutna)
CREATE (kfc_m3_cat1:Category {name: 'Kofice'})
CREATE (kfc_m3_cat2:Category {name: 'Burgeri'})
CREATE (kfc_m3_cat3:Category {name: 'Wrapperi'})
CREATE (kfc_m3_cat4:Category {name: 'Dodaci'})

CREATE (kfc_m3)-[:HAS_CATEGORY {order: 1}]->(kfc_m3_cat1)
CREATE (kfc_m3)-[:HAS_CATEGORY {order: 2}]->(kfc_m3_cat2)
CREATE (kfc_m3)-[:HAS_CATEGORY {order: 3}]->(kfc_m3_cat3)
CREATE (kfc_m3)-[:HAS_CATEGORY {order: 4}]->(kfc_m3_cat4)

CREATE (kfc_m3_i1:MenuItem {
    name: 'Original Recipe Kofice 3kom', price: 820.0, calories: 620,
    description: 'Originalni KFC recept, hrskava korica', quantity: 3, unit: 'kom',
    timeMin: 8, timeMax: 12
})
CREATE (kfc_m3_i2:MenuItem {
    name: 'Hot Wings 6kom', price: 880.0, calories: 710,
    description: 'Ljuta pileca krila', quantity: 6, unit: 'kom',
    timeMin: 10, timeMax: 15
})
CREATE (kfc_m3_i3:MenuItem {
    name: 'Zinger Burger', price: 720.0, calories: 540,
    description: 'Ljuti piletina burger sa zelenom salatom', quantity: 1, unit: 'kom',
    timeMin: 5, timeMax: 8
})
CREATE (kfc_m3_i4:MenuItem {
    name: 'Double Down', price: 850.0, calories: 680,
    description: 'Dupla piletina umesto hleba', quantity: 1, unit: 'kom',
    timeMin: 8, timeMax: 12
})
CREATE (kfc_m3_i5:MenuItem {
    name: 'Twister Wrap', price: 750.0, calories: 490,
    description: 'Piletina, salata, majonez u tortilja', quantity: 1, unit: 'kom',
    timeMin: 5, timeMax: 8
})
CREATE (kfc_m3_i6:MenuItem {
    name: 'Coleslaw', price: 220.0, calories: 180,
    description: 'Kremasta kupus salata', quantity: 150, unit: 'g',
    timeMin: 1, timeMax: 2
})
CREATE (kfc_m3_i7:MenuItem {
    name: 'Pomfrit', price: 280.0, calories: 340,
    description: 'Hrskavi pomfrit', quantity: 150, unit: 'g',
    timeMin: 3, timeMax: 5
})

CREATE (kfc_m3_cat1)-[:INCLUDES_ITEM {discount: 0.0}]->(kfc_m3_i1)
CREATE (kfc_m3_cat1)-[:INCLUDES_ITEM {discount: 0.1, discountFrom: date('2026-03-01'), discountTo: date('2026-04-01')}]->(kfc_m3_i2)
CREATE (kfc_m3_cat2)-[:INCLUDES_ITEM {discount: 0.0}]->(kfc_m3_i3)
CREATE (kfc_m3_cat2)-[:INCLUDES_ITEM {discount: 0.0}]->(kfc_m3_i4)
CREATE (kfc_m3_cat3)-[:INCLUDES_ITEM {discount: 0.0}]->(kfc_m3_i5)
CREATE (kfc_m3_cat4)-[:INCLUDES_ITEM {discount: 0.0}]->(kfc_m3_i6)
CREATE (kfc_m3_cat4)-[:INCLUDES_ITEM {discount: 0.0}]->(kfc_m3_i7)

// Sladoled meni
CREATE (kfc_m4_cat1:Category {name: 'Sladoledi'})
CREATE (kfc_m4)-[:HAS_CATEGORY {order: 1}]->(kfc_m4_cat1)

CREATE (kfc_m4_i1:MenuItem {
    name: 'Soft Sladoled Vanila', price: 180.0, calories: 220,
    description: 'Kremasti soft sladoled', quantity: 1, unit: 'kom',
    timeMin: 1, timeMax: 2
})
CREATE (kfc_m4_i2:MenuItem {
    name: 'Sundae Cokolada', price: 250.0, calories: 380,
    description: 'Sladoled sa cokoladnim prelivom', quantity: 1, unit: 'kom',
    timeMin: 2, timeMax: 3
})
CREATE (kfc_m4_i3:MenuItem {
    name: 'Milkshake Jagoda', price: 320.0, calories: 450,
    description: 'Kremasti milkshake od jagode', quantity: 400, unit: 'ml',
    timeMin: 3, timeMax: 5
})

CREATE (kfc_m4_cat1)-[:INCLUDES_ITEM {discount: 0.0}]->(kfc_m4_i1)
CREATE (kfc_m4_cat1)-[:INCLUDES_ITEM {discount: 0.0}]->(kfc_m4_i2)
CREATE (kfc_m4_cat1)-[:INCLUDES_ITEM {discount: 0.15, discountFrom: date('2026-04-01'), discountTo: date('2026-06-01')}]->(kfc_m4_i3)

// Dorucak meni
CREATE (kfc_m5_cat1:Category {name: 'Egg'})
CREATE (kfc_m5)-[:HAS_CATEGORY {order: 1}]->(kfc_m5_cat1)

CREATE (kfc_m5_i1:MenuItem {
    name: 'Egg Burger', price: 420.0, calories: 480,
    description: 'Jaje, sir, slanina u pecivo', quantity: 1, unit: 'kom',
    timeMin: 5, timeMax: 8
})
CREATE (kfc_m5_i2:MenuItem {
    name: 'Hash Brown', price: 180.0, calories: 250,
    description: 'Hrskavi krompir kroketi', quantity: 2, unit: 'kom',
    timeMin: 3, timeMax: 5
})
CREATE (kfc_m5_i3:MenuItem {
    name: 'Dorucak Box', price: 680.0, calories: 720,
    description: 'Egg burger + hash brown + kafa', quantity: 1, unit: 'kom',
    timeMin: 7, timeMax: 10
})

CREATE (kfc_m5_cat1)-[:INCLUDES_ITEM {discount: 0.0}]->(kfc_m5_i1)
CREATE (kfc_m5_cat1)-[:INCLUDES_ITEM {discount: 0.0}]->(kfc_m5_i2)
CREATE (kfc_m5_cat1)-[:INCLUDES_ITEM {discount: 0.1}]->(kfc_m5_i3)


// TREĆI RESTORAN - LOFT, ovaj korstiti za jednostavne upite, samo jedan meni


CREATE (loft:Restaurant {
    name: 'Loft Coffee & Food',
    address: 'Njegoševa 2',
    contact: '021/300-400'
})

CREATE (loft_m1:Menu {
    menuId: 606, version: 1, name: 'Brunch Ponuda',
    description: 'Specijaliteti za kasni doručak', type: 'STANDARD',
    activationDate: date('2026-01-15')
})

CREATE (loft)-[:HAS_MENU {active: true}]->(loft_m1)

CREATE (loft_cat1:Category {name: 'Kafa i Piće'})
CREATE (loft_cat2:Category {name: 'Sendviči'})

CREATE (loft_m1)-[:HAS_CATEGORY {order: 1}]->(loft_cat1)
CREATE (loft_m1)-[:HAS_CATEGORY {order: 2}]->(loft_cat2)

CREATE (loft_i1:MenuItem {
    name: 'Cappuccino', price: 280.0, calories: 120,
    description: 'Espresso sa mlečnom penom', quantity: 200, unit: 'ml',
    timeMin: 3, timeMax: 5
})
CREATE (loft_i2:MenuItem {
    name: 'Avocado Toast', price: 720.0, calories: 450,
    description: 'Integralni hleb, avokado, jaje', quantity: 250, unit: 'g',
    timeMin: 10, timeMax: 12
})

CREATE (loft_cat1)-[:INCLUDES_ITEM {discount: 0.0}]->(loft_i1)
CREATE (loft_cat2)-[:INCLUDES_ITEM {discount: 0.1}]->(loft_i2)


// ČETVRTI RESTORAN - LANTERNA, isto kao prethodni

CREATE (lanterna:Restaurant {
    name: 'Lanterna',
    address: 'Dunavska 10',
    contact: '021/555-666'
})

CREATE (lanterna_m1:Menu {
    menuId: 707, version: 1, name: 'Glavni Meni',
    description: 'Italijanska kuhinja', type: 'STANDARD',
    activationDate: date('2025-12-01')
})

CREATE (lanterna)-[:HAS_MENU {active: true}]->(lanterna_m1)

CREATE (lanterna_cat1:Category {name: 'Specijaliteti kuće'})

CREATE (lanterna_m1)-[:HAS_CATEGORY {order: 1}]->(lanterna_cat1)

CREATE (lanterna_i1:MenuItem {
    name: 'Lasagne Emiliane', price: 1350.0, calories: 1100,
    description: 'Domaće lazanje sa bolonjeze sosom', quantity: 450, unit: 'g',
    timeMin: 20, timeMax: 30
})

CREATE (lanterna_cat1)-[:INCLUDES_ITEM {discount: 0.0}]->(lanterna_i1)

RETURN "Baza je popunjena sa 4 restorana";