<template>

  <div class="container-fluid">
    <div id="liveAlertPlaceholder"></div>
    <div class="row" style="justify-content: center">
      <div class="col-md-10 col-xl-6 " style="height: 100vh">
        <div class="d-flex w-100 h-100 flex-column justify-content-center align-items-center">
          <div class="w-75 text-center justify-content-center align-items-center d-flex flex-column" style="margin-bottom: 5%">
            <img src="../assets/items/tiger_logo.png" class="logotype img-fluid" alt="Tigerhost logotype">
          </div>
          <div class="login_form_forgot col-11 col-md-11 h-50 d-flex flex-column justify-content-center align-items-center">
                <form class="d-flex w-100 h-100 flex-column justify-content-center align-items-center">
                 <div class="d-flex flex-column h-100 w-100 justify-content-around align-items-center">
                  <div class="form-header">Восстановление пароля</div>
                  <div class="form-group align-items-center w-75">
                    <label class = "label_text text-lg-center text-sm-center" for="password_new">Введите пароль</label>
                    <input v-model="passwd" class = "form-control" type="password" id="password_new" style="background-color: lightgray; border-color: #e60109; border-width: 2px">
                  </div>
                  <div class="form-group align-items-center w-75">
                    <label class="label_text text-lg-center text-sm-center" for="password_confirm">Повторите пароль</label>
                    <input v-model=passwdConfirm class = "form-control" type="password" id="password_confirm" style="background-color: lightgray; border-color: #e60109; border-width: 2px">
                  </div>
                  <div class="form-group align-items-center mb-2">
                    <button type="button" @click="checkPasswords" class="button_forgot_login">Сохранить</button>
                  </div>
                 </div>
                </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>



<script>
import "../css/PasswordChangeCss.scss"
import axios from "axios";

let alertPlaceholder;
export default {

  data() {
    return {
      passwdConfirm: "",
      passwd: "",
      mapResponse: [],
      mapUserEl: [],
      firstValidBlock: false,
      secondValidBlock: false,
      previousAlert: ""
    }
  },

  mounted() {
    const regexPswd = /^(?=.*\d)(?=.*[a-zA-Z]).[^!@#$%\-+^&*|\\/\s]{8,20}$/;

    document.getElementById("password_new").addEventListener('change', ev => {
      if (!regexPswd.test(this.passwd)) {
        this.firstValidBlock = false;
        this.createAlert("Некорректный пароль. Его длинна должна быть не менее 8 и не более 20 символов, также должен включать цифры и заглавные буквы", "danger")
      } else
        this.firstValidBlock = true;
    })
    document.getElementById("password_confirm").addEventListener('change', ev => {
      if (!regexPswd.test(this.passwdConfirm)) {
        this.secondValidBlock = false;
        this.createAlert("Некорректный пароль. Его длинна должна быть не менее 8 и не более 20 символов, также должен включать цифры и заглавные буквы", "danger")
      } else
        this.secondValidBlock = true;
    })
  },

  methods: {
    createAlert(message, type) {
      const wrapper = document.createElement('div')
      const uniqueId = Math.round(Math.random() * Math.pow(10, 17));

      if (document.getElementById(this.previousAlert))
        document.getElementById(this.previousAlert).remove()

      wrapper.innerHTML = [
        `<div class="alert alert-${type} text-center w-100 top-0 start-0 position-absolute" role="alert" id="${uniqueId}">`,
        `   <div>${message}</div>`,
        '</div>'
      ].join('')
      document.getElementById('liveAlertPlaceholder').append(wrapper)
      this.closeAlert(uniqueId)
      this.previousAlert = uniqueId

    },

    checkPasswords() {
      if (this.passwd === "" || this.passwdConfirm === "") {
        this.createAlert("Не все поля заполнены, проверьте правильность заполнения!", "warning")
      } else if (this.passwd !== this.passwdConfirm) {
        this.createAlert("Введенные пароли не совпадают, проверьте правильность заполнения!", "danger")
      } else if (this.secondValidBlock && this.firstValidBlock)
        this.changePassword(this.passwd, this.passwdConfirm, this.$route.params.code.substring(6))
    },

    closeAlert(alertId) {
      setTimeout(() => {
        if (document.getElementById(alertId))
          document.getElementById(alertId).remove()
      }, 8000)
    },

    async changePassword(newPassword, confirmPassword, code) {
      console.log(code)
      axios.put("http://localhost:8081/account/check", {
        "password": newPassword,
        "passwordConfirm": confirmPassword,
        "code": code
      }).then(response => {
        if (response.status === 200) {
          this.createAlert("Password change success!", "success")
        }
      }).catch(error => {
        console.log(error.response.status)
        if (error.response.status === 400) {
          this.createAlert(error.message, "danger")
        }
      })
    }
  }
}

</script>