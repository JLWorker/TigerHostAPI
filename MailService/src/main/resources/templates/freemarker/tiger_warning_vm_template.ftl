<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<div style="align-items: center; display:flex; flex-direction: column; width: 100%">
    <img style="width: 90%; margin-bottom: 3%; height: 10%; max-width: 500px; align-items: center" src="cid:tiger_logo" alt="Logotype"/>
    <h1 style="font-size: 1.5em">Срок услуги скоро истечет!</h1>

        <div style="width: 95%;
        height: auto;
        border-radius: 15px;
        border-color: #e30512;
        border-width: 3px;
        border-style: solid;
        max-width: 500px;
        text-align: center">

            <h4>Виртуальная машина №${vm_id}</h4>
            <h4>Дата окончания услуги - <mark style="color: black; background-color: yellow">${expired_date}</mark></h4>
            <h4>Стоимость - ${price}Р</h4>
            <p style="color: #e30512; font-weight: bolder; text-align: center">Продлите услугу, в противном случае работа виртуальной машины будет приостановлена!</p>
        </div>
    </div>
</div>
</body>
</html>