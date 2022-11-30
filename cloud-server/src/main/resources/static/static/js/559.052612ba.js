"use strict";(self["webpackChunkfrontend"]=self["webpackChunkfrontend"]||[]).push([[559],{1765:function(e,t,i){i.r(t),i.d(t,{default:function(){return X}});var s=i(3396),o=i(7139),a=i(9242),l=i(2874),r=i(2007);const n={class:"account-pages my-5 pt-sm-5"},d={class:"container"},c={class:"row justify-content-center"},u={class:"col-md-10"},m={class:"text-center mb-5"},p=(0,s._)("img",{src:l,alt:"",height:"28",class:"auth-logo-dark"},null,-1),w=(0,s._)("img",{src:r,alt:"",height:"28",class:"auth-logo-light"},null,-1),g=(0,s._)("p",{class:"font-size-15 text-muted mt-3"},[(0,s.Uk)(" Responsive "),(0,s._)("b",null,"Bootstrap 5"),(0,s.Uk)(" Admin Dashboard ")],-1),h={class:"card overflow-hidden"},v={class:"row g-0"},f={class:"col-lg-6"},_={class:"p-lg-5 p-4"},b=(0,s._)("div",null,[(0,s._)("h5",null,"Welcome Back !"),(0,s._)("p",{class:"text-muted"},"Sign in to continue to Samply.")],-1),k={class:"mt-4 pt-3"},y={class:"mb-3"},U=(0,s._)("label",{for:"email",class:"fw-semibold"},"Email",-1),D={key:0,class:"invalid-feedback"},$={key:0},x={key:1},C={class:"mb-3 mb-4"},q=(0,s._)("label",{for:"userpassword",class:"fw-semibold"},"Password",-1),A={key:0,class:"invalid-feedback"},E={class:"row align-items-center"},W={class:"col-6"},z=(0,s._)("i",{class:"bx bxl-google"},null,-1),I=(0,s.Uk)(" Google 계정 로그인"),L=(0,s._)("div",{class:"col-6"},[(0,s._)("div",{class:"text-end"},[(0,s._)("button",{class:"btn btn-primary w-md waves-effect waves-light",type:"submit"}," Log In ")])],-1),S={class:"mt-4"},V=(0,s._)("i",{class:"mdi mdi-lock me-1"},null,-1),P=(0,s.Uk)(" Forgot your password?"),F=(0,s._)("div",{class:"col-lg-6"},[(0,s._)("div",{class:"p-lg-5 p-4 bg-auth h-100 d-none d-lg-block"},[(0,s._)("div",{class:"bg-overlay"})])],-1),j={class:"mt-5 text-center"},B=(0,s.Uk)(" Don't have an account ? "),N=(0,s.Uk)(" Sign up "),G=(0,s._)("b",null,"Samply",-1),M=(0,s.Uk)(". Crafted with "),O=(0,s._)("i",{class:"mdi mdi-heart text-danger"},null,-1),R=(0,s.Uk)(" by Pichforest ");function T(e,t,i,l,r,T){const Y=(0,s.up)("router-link"),Z=(0,s.up)("b-alert"),H=(0,s.up)("b-button");return(0,s.wg)(),(0,s.iD)("div",n,[(0,s._)("div",d,[(0,s._)("div",c,[(0,s._)("div",u,[(0,s._)("div",m,[(0,s.Wm)(Y,{to:"/",class:"auth-logo"},{default:(0,s.w5)((()=>[p,w])),_:1}),g]),(0,s._)("div",h,[(0,s._)("div",v,[(0,s._)("div",f,[(0,s._)("div",_,[b,T.notification.message?((0,s.wg)(),(0,s.j4)(Z,{key:0,variant:T.notification.type,class:"mt-3",show:T.notificationAutoCloseDuration,dismissible:"",onDismissed:e.clear},{default:(0,s.w5)((()=>[(0,s.Uk)((0,o.zw)(T.notification.message),1)])),_:1},8,["variant","show","onDismissed"])):(0,s.kq)("",!0),(0,s.Wm)(Z,{variant:"danger",class:"mt-3",modelValue:r.isAuthError,"onUpdate:modelValue":t[0]||(t[0]=e=>r.isAuthError=e),show:T.notificationAutoCloseDuration,dismissible:"",onDismissed:e.clear},{default:(0,s.w5)((()=>[(0,s.Uk)((0,o.zw)(r.authError),1)])),_:1},8,["modelValue","show","onDismissed"]),(0,s._)("div",k,[(0,s._)("form",{onSubmit:t[3]||(t[3]=(0,a.iM)(((...e)=>T.login&&T.login(...e)),["prevent"]))},[(0,s._)("div",y,[U,(0,s.wy)((0,s._)("input",{type:"text",class:(0,o.C_)(["form-control",{"is-invalid":r.submitted&&e.$v.email.$error}]),"onUpdate:modelValue":t[1]||(t[1]=e=>r.email=e),id:"email",placeholder:"Enter email"},null,2),[[a.nr,r.email]]),r.submitted&&e.$v.email.$error?((0,s.wg)(),(0,s.iD)("div",D,[e.$v.email.required?(0,s.kq)("",!0):((0,s.wg)(),(0,s.iD)("span",$,"Email is required.")),e.$v.email.email?(0,s.kq)("",!0):((0,s.wg)(),(0,s.iD)("span",x,"Please enter valid email."))])):(0,s.kq)("",!0)]),(0,s._)("div",C,[q,(0,s.wy)((0,s._)("input",{type:"password","onUpdate:modelValue":t[2]||(t[2]=e=>r.password=e),class:(0,o.C_)(["form-control",{"is-invalid":r.submitted&&e.$v.password.$error}]),id:"userpassword",placeholder:"Enter password"},null,2),[[a.nr,r.password]]),r.submitted&&!e.$v.password.required?((0,s.wg)(),(0,s.iD)("div",A," Password is required. ")):(0,s.kq)("",!0)]),(0,s._)("div",E,[(0,s._)("div",W,[(0,s.Wm)(H,{variant:"outline-primary",onClick:T.googleLogin},{default:(0,s.w5)((()=>[z,I])),_:1},8,["onClick"])]),L]),(0,s._)("div",S,[(0,s.Wm)(Y,{to:"/auth/recoverpwd-1",class:"text-muted"},{default:(0,s.w5)((()=>[V,P])),_:1})])],32)])])]),F])]),(0,s._)("div",j,[(0,s._)("p",null,[B,(0,s.Wm)(Y,{to:"/register",class:"fw-semibold text-decoration-underline text-primary"},{default:(0,s.w5)((()=>[N])),_:1})]),(0,s._)("p",null,[(0,s.Uk)(" © "+(0,o.zw)((new Date).getFullYear())+" ",1),G,M,O,R])])])])])])}var Y=i(5795),Z=i(2949),H=i(8059),J={page:{title:"Login",meta:[{name:"description",content:H.W}]},data(){return{credentials:{userId:"",password:""},email:"admin@pichforest.com",password:"123456",submitted:!1,authError:null,tryingToLogIn:!1,isAuthError:!1}},computed:{notification(){return this.$store?this.$store.state.notification:null},notificationAutoCloseDuration(){return this.$store&&this.$store.state.notification?5:0}},validations:{email:{required:Y.C1,email:Y.Do},password:{required:Y.C1}},methods:{...Z.tA,...Z.DF,...Z.h7,googleLogin(){const e=this;window.location.href=e.googleApiUrl},async login(){const e=this;this.$_app.login(e.credentials.userId,e.credentials.password).then((e=>{window.location.href="/"})).catch((e=>{alert(e)}))}},mounted(){const e=this;e.href=window.location.href.substr(0,window.location.href.lastIndexOf("/")),e.hostName=e.href+"/oauth2/authorize/google",e.redirectUri=e.href+"/oauth2/redirect",e.googleApiUrl=e.hostName+"?redirect_uri="+e.redirectUri}},K=i(89);const Q=(0,K.Z)(J,[["render",T]]);var X=Q}}]);
//# sourceMappingURL=559.052612ba.js.map