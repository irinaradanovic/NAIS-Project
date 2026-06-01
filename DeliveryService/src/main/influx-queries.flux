// QUERY 1
// Najoptereceniji dostavljaci

from(bucket:"delivery_tracking")
  |> range(start:-30d)
  |> filter(fn:(r) => r._field == "activeOrders")
  |> group(columns:["courierId"])
  |> mean()
  |> sort(columns:["_value"], desc:true)


// QUERY 2
// Prosečno vreme dostave po gradu

from(bucket:"delivery_tracking")
  |> range(start:-30d)
  |> filter(fn:(r) => r._field == "deliveryDuration")
  |> group(columns:["city"])
  |> mean()
  |> sort(columns:["_value"])


// QUERY 3
// Distribucija statusa dostavljača

from(bucket:"delivery_tracking")
  |> range(start:-30d)
  |> group(columns:["status"])
  |> count()
  |> sort(columns:["_value"], desc:true)