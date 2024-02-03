<template>

  <div class="container-fluid">
    <div class="row" style="justify-content: center">
      <div class="col-md-10 col-xl-6 " style="height: 100vh">
        <div class="d-flex justify-content-center align-items-center" style="width: 100%; height: 100%">
          <div class="login_form_forgot d-flex flex-column align-items-center justify-content-around">
            <div class="d-flex flex-column align-items-center justify-content-around">
              <div class="form-header mb-4">Восстановление пароля</div>
                <form class="w-100 d-flex flex-column justify-content-around" style="height: 40%">
                  <div class="form-group align-items-center mb-4">
                    <label class = "label_text text-lg-center text-sm-center" for="password_new">Введите пароль</label>
                    <input class = "form-control" type="password" id="password_new" style="background-color: lightgray; border-color: #e60109; border-width: 2px">
                  </div>
                  <div class="form-group align-items-center mb-4">
                    <label class="label_text text-lg-center text-sm-center" for="password_confirm">Повторите пароль</label>
                    <input class = "form-control" type="password" id="password_new" style="background-color: lightgray; border-color: #e60109; border-width: 2px">
                  </div>
                  <div class="form-group align-items-center">
                    <button type="submit" class="button_forgot_login">Сохранить</button>
                  </div>
                </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>



<script>
import "../css/PasswordChangeCss.scss"
export default {
  data(){
    return {
      flag: false,
      login: "",
      passwd: "",
      error_flag: false,
      mapResponse: [],
      mapUserEl: []
    }
  },
  methods: {
    //
    // async loginUser(login, passwd){
    //   axios.post("http://localhost:8000/auth/api/login", {
    //     "username": login,
    //     "password": passwd
    //   }).then(response => {
    //     this.mapUserEl = response.data;
    //     localStorage.username = this.mapUserEl.username;
    //     localStorage.token = this.mapUserEl.tokenLogin;
    //     localStorage.role = this.mapUserEl.role;
    //     if(response.status === 200){
    //       console.log(localStorage.token);
    //       Main.data().startPage = true;
    //       router.push("/main");
    //     }
    //   }).catch(error => {
    //     console.log(error.response.status)
    //     if(error.response.status === 401){
    //       this.error_flag=true;
    //       setTimeout(()=>{this.error_flag = false}, 10000);
    //     }
    //   })
    },

    showForgotWindow(e){
      this.flag = !this.flag;
    },
  }

</script>

<!--<style lang="scss">-->

<!--.forgot_pass{-->
<!--  color: #FF0DFF;-->
<!--  font-family: "Base", sans-serif;-->
<!--  cursor: pointer;-->

<!--}-->

<!--</style>-->
