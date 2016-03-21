var FormValidation = function () {


    return {
        //main function to initiate the module
        init: function () {

            // for more info visit the official plugin documentation: 
            // http://docs.jquery.com/Plugins/Validation

            var formPrivilegeAdmin = $('#form-privilegeAdmin');

            formPrivilegeAdmin.validate({
                errorElement: 'span', //default input error message container
                errorClass: 'help-inline', // default input error message class
                focusInvalid: false, // do not focus the last invalid input
                ignore: "",
                rules: {
                    optionAdmin: {
                        required: true
                    },
                    password:{
                        minlength: 5,
                        required: true
                    },
                    confirm_password:{
                        minlength: 5,
                        required: true,
                        equalTo: "#password"
                    }
                },

                highlight: function (element) { // hightlight error inputs
                    $(element)
                        .closest('.help-inline').removeClass('ok'); // display OK icon
                    $(element)
                        .closest('.control-group').removeClass('success').addClass('error'); // set error class to the control group
                },

                unhighlight: function (element) { // revert the change dony by hightlight
                    $(element)
                        .closest('.control-group').removeClass('error'); // set error class to the control group
                },

                success: function (label) {
                    label
                        .addClass('valid').addClass('help-inline ok') // mark the current input as valid and display OK icon
                    .closest('.control-group').removeClass('error').addClass('success'); // set success class to the control group
                },

                submitHandler: function (form) {
                    $("body").msgBox({
                        status: "info", 
                        msg:"成功保存",
                        time:600,
                        end:function(){   
                            $('#JadminAccount').show();
                            formPrivilegeAdmin.hide();
                        }
                    });
                }
            });
            
        }

    };

}();