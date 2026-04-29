CREATE (a1:Article { id: randomUUID(), name: 'Pizza Margherita', price: 900.0, isAvailable: true })
CREATE (a2:Article { id: randomUUID(), name: 'Coca Cola', price: 150.0, isAvailable: true })
CREATE (a3:Article { id: randomUUID(), name: 'Burger', price: 650.0, isAvailable: true })
CREATE (a4:Article { id: randomUUID(), name: 'Fries', price: 250.0, isAvailable: true })
CREATE (a5:Article { id: randomUUID(), name: 'Pasta Carbonara', price: 1200.0, isAvailable: true })

CREATE (o1:Order {
  id: randomUUID(),
  creationDate: localdatetime('2026-04-01T10:00:00'),
  status: 'CREATED',
  orderType: 'DELIVERY',
  address: 'Bulevar Oslobodjenja 10'
})
CREATE (o2:Order {
  id: randomUUID(),
  creationDate: localdatetime('2026-04-02T12:30:00'),
  status: 'IN_PROGRESS',
  orderType: 'PICKUP',
  address: 'Trg Slobode 3'
})
CREATE (o3:Order {
  id: randomUUID(),
  creationDate: localdatetime('2026-04-03T18:15:00'),
  status: 'COMPLETED',
  orderType: 'DELIVERY',
  address: 'Futoska 25'
})

CREATE (o1)-[:HAS_ARTICLE {quantity: 2}]->(a1)
CREATE (o1)-[:HAS_ARTICLE {quantity: 2}]->(a2)

CREATE (o2)-[:HAS_ARTICLE {quantity: 1}]->(a3)
CREATE (o2)-[:HAS_ARTICLE {quantity: 1}]->(a4)

CREATE (o3)-[:HAS_ARTICLE {quantity: 1}]->(a5)

CREATE (i1:Invoice { id: randomUUID(), price: 2100.0, issueDate: date('2026-04-01') })
CREATE (i2:Invoice { id: randomUUID(), price: 900.0,  issueDate: date('2026-04-02') })
CREATE (i3:Invoice { id: randomUUID(), price: 1200.0, issueDate: date('2026-04-03') })

CREATE (o1)-[:HAS_INVOICE]->(i1)
CREATE (o2)-[:HAS_INVOICE]->(i2)
CREATE (o3)-[:HAS_INVOICE]->(i3)