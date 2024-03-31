<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<div style="align-items: center; display:flex; flex-direction: column; width: 100%; height: 100%">
    <img style="width: 90%; margin-bottom: 3%; height: 10%; max-width: 500px; align-items: center" src="cid:tiger_logo" alt="Logotype"/>
    <h1 style="width: 90%; text-align: center; height: auto; font-size: 1.5em; margin-bottom: 1.5em">Для изменения пароля нажмите на ссылку ниже</h1>
    <a href="${url}" style="color: #e30512; font-size: 2em; font-weight: bolder; margin-bottom: 1em; text-underline: none" onclick=sendLink(${url})>
            Восстановить
    </a>
    <p style="color: #e30512; font-weight: bolder; text-align: center">Ссылка действительна в течении 1 часа!</p>
</div>
</body>
</html>