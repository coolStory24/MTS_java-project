<html lang="ru">
<head>
  <meta charset="UTF-8">
  <title>Главная страница</title>
  <link rel="stylesheet"
        href="https://cdn.jsdelivr.net/gh/yegor256/tacit@gh-pages/tacit-css-1.6.0.min.css"/>
</head>

<body>

<h1>Бронирования</h1>
<h2>Комната №${roomId}. ${date}</h2>

<table>
  <tr>
    <th>Начало</th>
    <th>Окончание</th>
    <th>ID пользователя</th>
  </tr>
    <#list reservations as reservation>
      <tr>
        <td>${reservation.start}</td>
        <td>${reservation.end}</td>
        <td>${reservation.userId}</td>
        <br/>
      </tr>
    </#list>
</table>

</body>

</html>