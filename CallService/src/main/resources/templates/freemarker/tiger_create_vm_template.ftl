<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<div style="align-items: center; display:flex; flex-direction: column; width: 100%">
    <img style="width: 90%; margin-bottom: 3%; height: 10%; max-width: 500px; align-items: center" src="cid:tiger_logo" alt="Logotype"/>

    <h1 style="font-size: 1.5em; margin-bottom: 1%">Виртуальная машина создана!</h1>
    <h1 style="margin-bottom: 3%; font-size: 1.5em">ID: ${vm_id}</h1>
    <div style="background-color: white;
        width: 95%;
        height: auto;
        border-radius: 15px;
        border-color: #e30512;
        border-width: 3px;
        border-style: solid;
        max-width: 500px;
        text-align: center">

        <h4>Ваши данные для подключения:</h4>
        <h4>IP адресс - ${ip}</h4>
        <h4>Порт - ${port}</h4>
        <h4>Имя пользователя - ${username}</h4>
        <h4>Пароль - ${password}</h4>
        <p style="color: #e30512; text-align:center; font-weight: bolder">После входа обязательно создайте нового пользователя в системе!</p>
    </div>
</div>
</body>
</html>