var App = function () {

    // IE mode
    var isRTL = false;
    var isIE8 = false;
    var isIE9 = false;
    var isIE10 = false;

    var sidebarWidth = 225;
    var sidebarCollapsedWidth = 35;

    var responsiveHandlers = [];

    // theme layout color set
    /*var layoutColorCodes = {
        'blue': '#4b8df8',
        'red': '#e02222',
        'green': '#35aa47',
        'purple': '#852b99',
        'grey': '#555555',
        'light-grey': '#fafafa',
        'yellow': '#ffb848'
    };*/

    var handleInit = function() {

        if ($('body').css('direction') === 'rtl') {
            isRTL = true;
        }

        isIE8 = !! navigator.userAgent.match(/MSIE 8.0/);
        isIE9 = !! navigator.userAgent.match(/MSIE 9.0/);
        isIE10 = !! navigator.userAgent.match(/MSIE 10/);
        
        if (isIE10) {
            jQuery('html').addClass('ie10'); // detect IE10 version
        }
    }

    var handleDesktopTabletContents = function () {
        // loops all page elements with "responsive" class and applies classes for tablet mode
        // For metornic  1280px or less set as tablet mode to display the content properly
        if ($(window).width() <= 1280 || $('body').hasClass('page-boxed')) {
            $(".responsive").each(function () {
                var forTablet = $(this).attr('data-tablet');
                var forDesktop = $(this).attr('data-desktop');
                if (forTablet) {
                    $(this).removeClass(forDesktop);
                    $(this).addClass(forTablet);
                }
            });
        }

        // loops all page elements with "responsive" class and applied classes for desktop mode
        // For metornic  higher 1280px set as desktop mode to display the content properly
        if ($(window).width() > 1280 && $('body').hasClass('page-boxed') === false) {
            $(".responsive").each(function () {
                var forTablet = $(this).attr('data-tablet');
                var forDesktop = $(this).attr('data-desktop');
                if (forTablet) {
                    $(this).removeClass(forTablet);
                    $(this).addClass(forDesktop);
                }
            });
        }
    }

    var handleSidebarState = function () {
        // remove sidebar toggler if window width smaller than 900(for table and phone mode)
        if ($(window).width() < 980) {
            $('body').removeClass("page-sidebar-closed");
        }
    }

    var runResponsiveHandlers = function () {
        // reinitialize other subscribed elements
        for (var i in responsiveHandlers) {
            var each = responsiveHandlers[i];
            each.call();
        }
    }

    var handleResponsive = function () {
        handleTooltips();
        handleSidebarState();
        handleDesktopTabletContents();
        handleSidebarAndContentHeight();
        handleChoosenSelect();
        handleFixedSidebar();
        runResponsiveHandlers();
    }

    var handleResponsiveOnInit = function () {
        handleSidebarState();
        handleDesktopTabletContents();
        handleSidebarAndContentHeight();
    }

    var handleResponsiveOnResize = function () {
        var resize;
        if (isIE8) {
            var currheight;
            $(window).resize(function() {
                if(currheight == document.documentElement.clientHeight) {
                    return; //quite event since only body resized not window.
                }                
                if (resize) {
                    clearTimeout(resize);
                }   
                resize = setTimeout(function() {
                    handleResponsive();    
                }, 50); // wait 50ms until window resize finishes.                
                currheight = document.documentElement.clientHeight; // store last body client height
            });
        } else {
            $(window).resize(function() {
                if (resize) {
                    clearTimeout(resize);
                }   
                resize = setTimeout(function() {
                    console.log('resize');
                    handleResponsive();    
                }, 50); // wait 50ms until window resize finishes.
            });
        }   
    }

    //* BEGIN:CORE HANDLERS *//
    // this function handles responsive layout on screen size resize or mobile device rotate.
  
    var handleSidebarAndContentHeight = function () {
        var content = $('.page-content');
        var sidebar = $('.page-sidebar');
        var body = $('body');
        var iframe = $("#mainFrame");
        var height;

        if (body.hasClass("page-footer-fixed") === true && body.hasClass("page-sidebar-fixed") === false) {
            var available_height = $(window).height() - $('.footer').height();
            if (content.height() <  available_height) {
            	content.attr('style', 'height:' + available_height + 'px !important');
            }
        } else {
            if (body.hasClass('page-sidebar-fixed')) {
                height = _calculateFixedSidebarViewportHeight();
                if(body.hasClass('page-header-fixed')){
                    height = _calculateFixedSidebarViewportHeight()-20;
                    
                }
            } else {
                height = sidebar.height();
            }
            if (height >= content.height()) {
                content.attr('style', 'height:' + height + 'px !important');
            } 
            var iframe_height = content.height();
            iframe.attr('style', 'height:' + iframe_height + 'px !important');
            $(".modal-detail-slider .modal-body").height(iframe_height);
        }   
        //iframe 自适应高度
       /* iframe.load(function(){
        	var mainheight = $(this).contents().find("body").height()+30;
        	iframe.height(mainheight);
        }); */
        
    }

    var handleSidebarMenu = function () {
        jQuery('.page-sidebar').on('click', 'li > a', function (e) {
                if ($(this).next().hasClass('sub-menu') == false) {
                    if ($('.btn-navbar').hasClass('collapsed') == false) {
                        $('.btn-navbar').click();
                    }
                    return;
                }

                var parent = $(this).parent().parent();

                parent.children('li.open').children('a').children('.arrow').removeClass('open');
                parent.children('li.open').children('.sub-menu').slideUp(200);
                parent.children('li.open').removeClass('open');

                var sub = jQuery(this).next();
                if (sub.is(":visible")) {
                    jQuery('.arrow', jQuery(this)).removeClass("open");
                    jQuery(this).parent().removeClass("open");
                    sub.slideUp(200, function () {
                            handleSidebarAndContentHeight();
                        });
                } else {
                    jQuery('.arrow', jQuery(this)).addClass("open");
                    jQuery(this).parent().addClass("open");
                    sub.slideDown(200, function () {
                            handleSidebarAndContentHeight();
                        });
                }

                e.preventDefault();
            });

        // handle ajax links
        jQuery('.page-sidebar').on('click', ' li > a.ajaxify', function (e) {
                e.preventDefault();
                App.scrollTop();

                var url = $(this).attr("href");
                var menuContainer = jQuery('.page-sidebar ul');
                var pageContent = $('.page-content');
                var pageContentBody = $('.page-content .page-content-body');

                menuContainer.children('li.active').removeClass('active');
                menuContainer.children('arrow.open').removeClass('open');

                $(this).parents('li').each(function () {
                        $(this).addClass('active');
                        $(this).children('a > span.arrow').addClass('open');
                    });
                $(this).parents('li').addClass('active');

                App.blockUI(pageContent, false);

                $.post(url, {}, function (res) {
                        App.unblockUI(pageContent);
                        pageContentBody.html(res);
                        App.fixContentHeight(); // fix content height
                        App.initUniform(); // initialize uniform elements
                    });
            });
    }

    var _calculateFixedSidebarViewportHeight = function () {
        var sidebarHeight = $(window).height() - $('.header').height() + 1;
        if ($('body').hasClass("page-footer-fixed")) {
            sidebarHeight = sidebarHeight - $('.footer').height();
        }

        return sidebarHeight; 
    }

    var handleFixedSidebar = function () {
        var menu = $('.page-sidebar-menu');

        if (menu.parent('.slimScrollDiv').size() === 1) { // destroy existing instance before updating the height
            menu.slimScroll({
                destroy: true
            });
            menu.removeAttr('style');
            $('.page-sidebar').removeAttr('style');            
        }

        if ($('.page-sidebar-fixed').size() === 0) {
            handleSidebarAndContentHeight();
            return;
        }

        if ($(window).width() >= 980) {
            var sidebarHeight = _calculateFixedSidebarViewportHeight();

            /*menu.slimScroll({
                size: '7px',
                color: '#a1b2bd',
                opacity: .3,
                position: isRTL ? 'left' : ($('.page-sidebar-on-right').size() === 1 ? 'left' : 'right'),
                height: sidebarHeight,
                allowPageScroll: false,
                disableFadeOut: false
            });*/
            handleSidebarAndContentHeight();
        }
    }

    var handleFixedSidebarHoverable = function () {
        if ($('body').hasClass('page-sidebar-fixed') === false) {
            return;
        }

        $('.page-sidebar').off('mouseenter').on('mouseenter', function () {
            var body = $('body');

            if ((body.hasClass('page-sidebar-closed') === false || body.hasClass('page-sidebar-fixed') === false) || $(this).hasClass('page-sidebar-hovering')) {
                return;
            }

            body.removeClass('page-sidebar-closed').addClass('page-sidebar-hover-on');
            $(this).addClass('page-sidebar-hovering');                
            $(this).animate({
                width: sidebarWidth
            }, 400, '', function () {
                $(this).removeClass('page-sidebar-hovering');
            });
        });

        $('.page-sidebar').off('mouseleave').on('mouseleave', function () {
            var body = $('body');

            if ((body.hasClass('page-sidebar-hover-on') === false || body.hasClass('page-sidebar-fixed') === false) || $(this).hasClass('page-sidebar-hovering')) {
                return;
            }

            $(this).addClass('page-sidebar-hovering');
            $(this).animate({
                width: sidebarCollapsedWidth
            }, 400, '', function () {
                $('body').addClass('page-sidebar-closed').removeClass('page-sidebar-hover-on');
                $(this).removeClass('page-sidebar-hovering');
            });
        });
    }

    var handleSidebarToggler = function () {
        // handle sidebar show/hide
        $('.page-sidebar').on('click', '.sidebar-toggler', function (e) {            
            var body = $('body');
            var sidebar = $('.page-sidebar');

            if ((body.hasClass("page-sidebar-hover-on") && body.hasClass('page-sidebar-fixed')) || sidebar.hasClass('page-sidebar-hovering')) {
                body.removeClass('page-sidebar-hover-on');
                sidebar.css('width', '').hide().show();
                e.stopPropagation();
                runResponsiveHandlers();
                return;
            }

            $(".sidebar-search", sidebar).removeClass("open");

            if (body.hasClass("page-sidebar-closed")) {
                body.removeClass("page-sidebar-closed");
                if (body.hasClass('page-sidebar-fixed')) {
                    sidebar.css('width', '');
                }
            } else {
                body.addClass("page-sidebar-closed");
            }
            runResponsiveHandlers();
        });

        // handle the search bar close
        $('.page-sidebar').on('click', '.sidebar-search .remove', function (e) {
            e.preventDefault();
            $('.sidebar-search').removeClass("open");
        });

        // handle the search query submit on enter press
        $('.page-sidebar').on('keypress', '.sidebar-search input', function (e) {
            if (e.which == 13) {
                window.location.href = "extra_search.html";
                return false; //<---- Add this line
            }
        });

        // handle the search submit
        $('.sidebar-search .submit').on('click', function (e) {
            e.preventDefault();
          
                if ($('body').hasClass("page-sidebar-closed")) {
                    if ($('.sidebar-search').hasClass('open') == false) {
                        if ($('.page-sidebar-fixed').size() === 1) {
                            $('.page-sidebar .sidebar-toggler').click(); //trigger sidebar toggle button
                        }
                        $('.sidebar-search').addClass("open");
                    } else {
                        window.location.href = "extra_search.html";
                    }
                } else {
                    window.location.href = "extra_search.html";
                }
        });
    }

    var handleHorizontalMenu = function () {
        //handle hor menu search form toggler click
        $('.header').on('click', '.hor-menu .hor-menu-search-form-toggler', function (e) {
                if ($(this).hasClass('hide')) {
                    $(this).removeClass('hide');
                    $('.header .hor-menu .search-form').hide();
                } else {
                    $(this).addClass('hide');
                    $('.header .hor-menu .search-form').show();
                }
                e.preventDefault();
            });

        //handle hor menu search button click
        $('.header').on('click', '.hor-menu .search-form .btn', function (e) {
                window.location.href = "extra_search.html";
                e.preventDefault();
            });

        //handle hor menu search form on enter press
        $('.header').on('keypress', '.hor-menu .search-form input', function (e) {
                if (e.which == 13) {
                    window.location.href = "extra_search.html";
                    return false;
                }
            });
    }

    var handleGoTop = function () {
        /* set variables locally for increased performance */
        jQuery('.footer').on('click', '.go-top', function (e) {
                App.scrollTo();
                e.preventDefault();
            });
    }

    var handlePortletTools = function () {
        jQuery('body').on('click', '.portlet .tools a.remove', function (e) {
            e.preventDefault();
                var removable = jQuery(this).parents(".portlet");
                if (removable.next().hasClass('portlet') || removable.prev().hasClass('portlet')) {
                    jQuery(this).parents(".portlet").remove();
                } else {
                    jQuery(this).parents(".portlet").parent().remove();
                }
        });

        jQuery('body').on('click', '.portlet .tools a.reload', function (e) {
            e.preventDefault();
                var el = jQuery(this).parents(".portlet");
                App.blockUI(el);
                window.setTimeout(function () {
                        App.unblockUI(el);
                    }, 1000);
        });

        jQuery('body').on('click', '.portlet .tools .collapse, .portlet .tools .expand', function (e) {
            e.preventDefault();
                var el = jQuery(this).closest(".portlet").children(".portlet-body");
                if (jQuery(this).hasClass("collapse")) {
                    jQuery(this).removeClass("collapse").addClass("expand");
                    el.slideUp(200);
                } else {
                    jQuery(this).removeClass("expand").addClass("collapse");
                    el.slideDown(200);
                }
        });
    }

    var handleUniform = function () {
        if (!jQuery().uniform) {
            return;
        }
        var test = $("input[type=checkbox]:not(.toggle), input[type=radio]:not(.toggle, .star)");
        if (test.size() > 0) {
            test.each(function () {
                    if ($(this).parents(".checker").size() == 0) {
                        $(this).show();
                        $(this).uniform();
                    }
                });
        }
    }

    var handleAccordions = function () {
        $(".accordion").collapse().height('auto');

        var lastClicked;

        //add scrollable class name if you need scrollable panes
        jQuery('body').on('click', '.accordion.scrollable .accordion-toggle', function () {
            lastClicked = jQuery(this);
        }); //move to faq section

        jQuery('body').on('shown', '.accordion.scrollable', function () {
            jQuery('html,body').animate({
                scrollTop: lastClicked.offset().top - 150
            }, 'slow');
        });
    }

    var handleTabs = function () {

        // function to fix left/right tab contents
        var fixTabHeight = function(tab) {
            $(tab).each(function() {
                var content = $($($(this).attr("href")));
                var tab = $(this).parent().parent();
                if (tab.height() > content.height()) {
                    content.css('min-height', tab.height());    
                } 
            });            
        }

        // fix tab content on tab shown
        $('body').on('shown', '.nav.nav-tabs.tabs-left a[data-toggle="tab"], .nav.nav-tabs.tabs-right a[data-toggle="tab"]', function(){
            fixTabHeight($(this)); 
        });

        $('body').on('shown', '.nav.nav-tabs', function(){
            handleSidebarAndContentHeight();
        });

        //fix tab contents for left/right tabs
        fixTabHeight('.nav.nav-tabs.tabs-left > li.active > a[data-toggle="tab"], .nav.nav-tabs.tabs-right > li.active > a[data-toggle="tab"]');

        //activate tab if tab id provided in the URL
        if(location.hash) {
            var tabid = location.hash.substr(1);
            $('a[href="#'+tabid+'"]').click();
        }
    }

    var handleScrollers = function () {
        $('.scroller').each(function () {
                $(this).slimScroll({
                        size: '7px',
                        color: '#a1b2bd',
                        position: isRTL ? 'left' : 'right',
                        height: $(this).attr("data-height"),
                        alwaysVisible: ($(this).attr("data-always-visible") == "1" ? true : false),
                        railVisible: ($(this).attr("data-rail-visible") == "1" ? true : false),
                        disableFadeOut: true
                    });
            });
    }

    var handleTooltips = function () {
        if (App.isTouchDevice()) { // if touch device, some tooltips can be skipped in order to not conflict with click events
            jQuery('.tooltips:not(.no-tooltip-on-touch-device)').tooltip();
        } else {
            jQuery('.tooltips').tooltip();
        }
    }

    var handleDropdowns = function () {
        $('body').on('click', '.dropdown-menu.hold-on-click', function(e){
            e.stopPropagation();
        })
    }

    var handlePopovers = function () {
        jQuery('.popovers').popover();
    }

    var handleChoosenSelect = function () {
        if (!jQuery().chosen) {
            return;
        }

        $(".chosen").each(function () {
            $(this).chosen({
                allow_single_deselect: $(this).attr("data-with-diselect") === "1" ? true : false
            });
        });
    }

    var handleFancybox = function () {
        if (!jQuery.fancybox) {
            return;
        }

        if (jQuery(".fancybox-button").size() > 0) {
            jQuery(".fancybox-button").fancybox({
                groupAttr: 'data-rel',
                prevEffect: 'none',
                nextEffect: 'none',
                closeBtn: true,
                helpers: {
                    title: {
                        type: 'inside'
                    }
                }
            });
        }
    }
/*
    var handleTheme = function () {

        var panel = $('.color-panel');

        if ($('body').hasClass('page-boxed') == false) {
            $('.layout-option', panel).val("fluid");
        }
        
        $('.sidebar-option', panel).val("default");
        $('.header-option', panel).val("fixed");
        $('.footer-option', panel).val("default"); 

        //handle theme layout
        var resetLayout = function () {
            $("body").
                removeClass("page-boxed").
                removeClass("page-footer-fixed").
                removeClass("page-sidebar-fixed").
                removeClass("page-header-fixed");

            $('.header > .navbar-inner > .container').removeClass("container").addClass("container-fluid");

            if ($('.page-container').parent(".container").size() === 1) {
                $('.page-container').insertAfter('.header');
            } 

            if ($('.footer > .container').size() === 1) {                        
                $('.footer').html($('.footer > .container').html());                        
            } else if ($('.footer').parent(".container").size() === 1) {                        
                $('.footer').insertAfter('.page-container');
            }

            $('body > .container').remove(); 
        }

        var lastSelectedLayout = '';

        var setLayout = function () {

            var layoutOption = $('.layout-option', panel).val();
            var sidebarOption = $('.sidebar-option', panel).val();
            var headerOption = $('.header-option', panel).val();
            var footerOption = $('.footer-option', panel).val(); 

            if (sidebarOption == "fixed" && headerOption == "default") {
                alert('Default Header with Fixed Sidebar option is not supported. Proceed with Default Header with Default Sidebar.');
                $('.sidebar-option', panel).val("default");
                sidebarOption = 'default';
            }

            resetLayout(); // reset layout to default state

            if (layoutOption === "boxed") {
                $("body").addClass("page-boxed");

                // set header
                $('.header > .navbar-inner > .container-fluid').removeClass("container-fluid").addClass("container");
                var cont = $('.header').after('<div class="container"></div>');

                // set content
                $('.page-container').appendTo('body > .container');

                // set footer
                if (footerOption === 'fixed' || sidebarOption === 'default') {
                    $('.footer').html('<div class="container">'+$('.footer').html()+'</div>');
                } else {
                    $('.footer').appendTo('body > .container');
                }            
            }

            if (lastSelectedLayout != layoutOption) {
                //layout changed, run responsive handler:
                runResponsiveHandlers();
            }
            lastSelectedLayout = layoutOption;

            //header
            if (headerOption === 'fixed') {
                $("body").addClass("page-header-fixed");
                $(".header").removeClass("navbar-static-top").addClass("navbar-fixed-top");
            } else {
                $("body").removeClass("page-header-fixed");
                $(".header").removeClass("navbar-fixed-top").addClass("navbar-static-top");
            }

            //sidebar
            if (sidebarOption === 'fixed') {
                $("body").addClass("page-sidebar-fixed");
            } else {
                $("body").removeClass("page-sidebar-fixed");
            }

            //footer 
            if (footerOption === 'fixed') {
                $("body").addClass("page-footer-fixed");
            } else {
                $("body").removeClass("page-footer-fixed");
            }

            handleSidebarAndContentHeight(); // fix content height            
            handleFixedSidebar(); // reinitialize fixed sidebar
            handleFixedSidebarHoverable(); // reinitialize fixed sidebar hover effect
        }

        // handle theme colors
        var setColor = function (color) {
            $('#style_color').attr("href", "assets/css/themes/" + color + ".css");
            $.cookie('style_color', color);                
        }

        $('.icon-color', panel).click(function () {
            $('.color-mode').show();
            $('.icon-color-close').show();
        });

        $('.icon-color-close', panel).click(function () {
            $('.color-mode').hide();
            $('.icon-color-close').hide();
        });

        $('li', panel).click(function () {
            var color = $(this).attr("data-style");
            setColor(color);
            $('.inline li', panel).removeClass("current");
            $(this).addClass("current");
        });

        $('.layout-option, .header-option, .sidebar-option, .footer-option', panel).change(setLayout);
    }
*/
    var handleFixInputPlaceholderForIE = function () {
        //fix html5 placeholder attribute for ie7 & ie8
        if (isIE8 || isIE9) { // ie7&ie8
            // this is html5 placeholder fix for inputs, inputs with placeholder-no-fix class will be skipped(e.g: we need this for password fields)
            jQuery('input[placeholder]:not(.placeholder-no-fix), textarea[placeholder]:not(.placeholder-no-fix)').each(function () {

                var input = jQuery(this);

                if(input.val()=='' && input.attr("placeholder") != '') {
                    input.addClass("placeholder").val(input.attr('placeholder'));
                }

                input.focus(function () {
                    if (input.val() == input.attr('placeholder')) {
                        input.val('');
                    }
                });

                input.blur(function () {                         
                    if (input.val() == '' || input.val() == input.attr('placeholder')) {
                        input.val(input.attr('placeholder'));
                    }
                });
            });
        }
    }
    //*add 2015 VYIYUN*//
    //账户退出
    var handleSignOut = function(){
    	$("#JsignOut").on("click",function(){
    		$.post ("../website/logout.action",{},function(data){
    			if(data.success){    				
    				//window.location.reload();
    				window.location.href="../website/index.html";
    			}
    		});
    	});
    }
    //时间控件
    var handleDatePickers = function () {

        if (jQuery().datepicker) {
            $('.date-picker').datepicker({
                format: 'yyyy-mm-dd',
                autoclose: true,
                todayBtn: 'linked',
            });
        }
    }
    //付款方式
    var handleTileSelect = function(){
        $(".tile").on("click",function(){
            $(this).addClass("selected").siblings().removeClass("selected");
        });
    }
    //邮寄地址
    var handleAddressCard = function(){
        $('.JaddressCardDel').on("click",function(){
            var card = $(this).parents(".invoice-address-card");
            card.remove();
        });
        $('.JaddressCardDefault').on("click",function(){
            var card = $(this).parents(".invoice-address-card");
            var cardTitle = card.find("h4");
            $(".invoice-address-card").find(".default").appendTo(cardTitle);
            card.prependTo(".invoice-address-block");
        });
    }


    //* END:CORE HANDLERS *//

    return {

        //main function to initiate template pages
        init: function () {

            //IMPORTANT!!!: Do not modify the core handlers call order.

            //core handlers
            handleInit();
            handleResponsiveOnResize(); // set and handle responsive    
            handleUniform();        
            handleScrollers(); // handles slim scrolling contents 
            handleResponsiveOnInit(); // handler responsive elements on page load

            //layout handlers
            handleFixedSidebar(); // handles fixed sidebar menu
            //handleFixedSidebarHoverable(); // handles fixed sidebar on hover effect 
            handleSidebarMenu(); // handles main menu
            handleHorizontalMenu(); // handles horizontal menu
            handleSidebarToggler(); // handles sidebar hide/show            
            handleFixInputPlaceholderForIE(); // fixes/enables html5 placeholder attribute for IE9, IE8
            handleGoTop(); //handles scroll to top functionality in the footer
           // handleTheme(); // handles style customer tool

            //ui component handlers
            handlePortletTools(); // handles portlet action bar functionality(refresh, configure, toggle, remove)
            handleDropdowns(); // handle dropdowns
            handleTabs(); // handle tabs
            handleTooltips(); // handle bootstrap tooltips
            handlePopovers(); // handles bootstrap popovers
            handleAccordions(); //handles accordions
            handleChoosenSelect(); // handles bootstrap chosen dropdowns    

            handleDatePickers();//handles bootstrap datePicker
            handleTileSelect();
            handleAddressCard();
            
            handleSignOut();
         

            App.addResponsiveHandler(handleChoosenSelect); // reinitiate chosen dropdown on main content resize. disable this line if you don't really use chosen dropdowns.
        },

        fixContentHeight: function () {
            handleSidebarAndContentHeight();
        },

        addResponsiveHandler: function (func) {
            responsiveHandlers.push(func);
        },

        // useful function to make equal height for contacts stand side by side
        setEqualHeight: function (els) {
            var tallestEl = 0;
            els = jQuery(els);
            els.each(function () {
                    var currentHeight = $(this).height();
                    if (currentHeight > tallestEl) {
                        tallestColumn = currentHeight;
                    }
                });
            els.height(tallestEl);
        },

        // wrapper function to scroll to an element
        scrollTo: function (el, offeset) {
            pos = el ? el.offset().top : 0;
            jQuery('html,body').animate({
                    scrollTop: pos + (offeset ? offeset : 0)
                }, 'slow');
        },

        scrollTop: function () {
            App.scrollTo();
        },

        // wrapper function to  block element(indicate loading)
        blockUI: function (el, centerY) {
            var el = jQuery(el); 
            el.block({
                    message: '<img src="./assets/img/ajax-loading.gif" align="">',
                    centerY: centerY != undefined ? centerY : true,
                    css: {
                        top: '10%',
                        border: 'none',
                        padding: '2px',
                        backgroundColor: 'none'
                    },
                    overlayCSS: {
                        backgroundColor: '#000',
                        opacity: 0.05,
                        cursor: 'wait'
                    }
                });
        },

        // wrapper function to  un-block element(finish loading)
        unblockUI: function (el) {
            jQuery(el).unblock({
                    onUnblock: function () {
                        jQuery(el).removeAttr("style");
                    }
                });
        },

        // initializes uniform elements
        initUniform: function (els) {

            if (els) {
                jQuery(els).each(function () {
                        if ($(this).parents(".checker").size() == 0) {
                            $(this).show();
                            $(this).uniform();
                        }
                    });
            } else {
                handleUniform();
            }

        },

        // initializes choosen dropdowns
        initChosenSelect: function (els) {
            $(els).chosen({
                    allow_single_deselect: true
                });
        },

        initFancybox: function () {
            handleFancybox();
        },

        getActualVal: function (el) {
            var el = jQuery(el);
            if (el.val() === el.attr("placeholder")) {
                return "";
            }

            return el.val();
        },

        getURLParameter: function (paramName) {
            var searchString = window.location.search.substring(1),
                i, val, params = searchString.split("&");

            for (i = 0; i < params.length; i++) {
                val = params[i].split("=");
                if (val[0] == paramName) {
                    return unescape(val[1]);
                }
            }
            return null;
        },

        // check for device touch support
        isTouchDevice: function () {
            try {
                document.createEvent("TouchEvent");
                return true;
            } catch (e) {
                return false;
            }
        },

        isIE8: function () {
            return isIE8;
        },

        isRTL: function () {
            return isRTL;
        }/*,

        getLayoutColorCode: function (name) {
            if (layoutColorCodes[name]) {
                return layoutColorCodes[name];
            } else {
                return '';
            }
        }
*/
    };
    
}();

/*
    zedd 
    2015.11
*/
// app detail slider 
var appModalSlider = function(){
	//请求应用数据
	var appDataUrl = "../manage/getAgentInfos.action";
	var appData;
	//应用排列
	var getAppData = function(data){
		if(data.success){
			appData = data.data.agentInfos;
			var appNum = appData.length,appHtml=[];
			for(var i=0;i<appNum;i++){
				
				var thisApp = appData[i],
					appImg = thisApp.squareLogoUrl,
					appManageUrl = thisApp.url,
					appName = thisApp.agentName;
					
				appHtml.push('<li>'+
		                '<div class="app-img">'+
		                '<a href="#"><img src="'+appImg+'" alt=""></a>'+
		                '<div class="app-tool">'+
		                	'<a href="'+appManageUrl+'" title="管理" class="JappManage"><i class="icon-cog"></i></a>'+
		                	'<a href="javascript:;" title="详情" class="JSlideDetail"><i class=" icon-th-list"></i></a>'+
		                '</div>'+
		            '</div>'+
		            '<div class="app-title"><a href="#">'+appName+'</a></div>'+
		        '</li>');
			}
			appHtml.join("");
			$(".app-fluid").prepend(appHtml);
		}
	}
	ajaxServer(appDataUrl,{},getAppData,{type : 'get'});

    var modalDetailSlider;
    var modalHtml = function(i){
        var no = i,
            thisData = appData[no],
            btn,appStatusTxt;                     
        var appName = thisData.agentName,
            appStatus = thisData.status,/*
            appExpire = thisData.expire,
            appPackage = thisData.package,*/
            appInfo = thisData.description;
        //是否已安装
            if(1 == appStatus){
                btn = '<button class="btn orange" data-dismiss="modal" type="btn">安装</button>';
                appStatusTxt = "取消授权";
            }else if(0 == appStatus){
                btn = '<button class="btn orange upgrade" type="btn">升级</button><button class="btn" data-dismiss="modal" type="btn">停用</button>';
                appStatusTxt = "已安装";
            }else{
                btn = '<button class="btn orange" data-dismiss="modal" type="btn">启用</button>';
                appStatusTxt = "已停用";
            }
        //定义我的弹出内容
        modalDetailSlider = '<div class="modal-detail-slider">\
                <div class="modal">\
                    <div class="modal-header">\
                        <button class="close" type="button"></button>\
                        <h4>应用详情</h4>\
                    </div>\
                    <div class="modal-body">\
                        <div class="row-fluid">\
                            <span class="span3 text-right bold">应用名称</span>\
                            <span class="span8">'+ appName +'</span>\
                        </div>\
                        <div class="row-fluid">\
                            <span class="span3 text-right bold">状态</span>\
                            <span class="span8">'+ appStatusTxt +'</span>\
                        </div>\
                        <div class="row-fluid">\
                            <span class="span3 text-right bold">应用详情</span>\
                            <span class="span8">'+ appInfo +'</span>\
                    </div>\
                </div>\
                <div class="modal-footer">'+ btn +'</div></div>';

    }
    //滑入效果
    var showModal = function(){   
        $(".frame-body").append(modalDetailSlider);
        $(".modal-detail-slider .modal-body").height($(window).height()-120);
        var modal = $(".modal-detail-slider").find(".modal");
        modal.animate({
            right:"0px"
        },200);
        
    }
    var closeModal = function(){
        var modal = $(".modal-detail-slider").find(".modal");
        modal.animate({
            right:"-500px"
           },200,function(){
           $(".modal-detail-slider").remove();
        });
    }
    //详情点击
    $(document).on("click",".JSlideDetail",function(e){
        e.stopPropagation();
        var no = $(this).parents('li').index();
        modalHtml(no);
        if($(".modal-detail-slider").size() != 0){
            var modal = $(".modal-detail-slider").find(".modal");
            modal.animate({
                right:"-500px"
               },200,function(){
               $(".modal-detail-slider").remove();
               showModal();  
            });
        }else{
            showModal();
        }        
    });
    //modal close
    $(document).on("click",".close",function(){
        closeModal();
    });
    $(document).on("click",function(e){
        closeModal();
    });
    //升级
    
    $(".page-container").on("click",".upgrade",function(){
        $("#upgradeModal").modal('show');
    });
  //管理点击
    $(document).on("click",".JappManage",function(e){
    	e.preventDefault();
        var url = $(this).attr("href");
       $(window.parent.document).find("#mainFrame").attr("src",url); 
    });
};

//table
var TableManaged = function(){
    return {
         init: function () {
            jQuery('.group-checkable').change(function () {
                var set = jQuery(this).attr("data-set");
                var checked = jQuery(this).is(":checked");
                jQuery(set).each(function () {
                    if (checked) {
                        $(this).prop("checked", true);
                    } else {
                        $(this).prop("checked", false);
                    }
                });
            });
            //
         }
    };
}();
//应用
/*var appModalSlider = function(){
	var modal = $(".modal-detail-slider").find(".modal");
	$(".modal-detail-slider .modal-body").height($(window).height()-215);
    //滑入效果
    var showModal = function(){
        modal.animate({
            right:"0px"
        },200);
        
    }
    var closeModal = function(){
        modal.animate({
            right:"-500px"
           },200);
    }
    //详情点击
    $(document).on("click",".JSlideDetail",function(e){
        e.stopPropagation();
        var thisAppIndex = $(this).parents("li").index();
        modal.attr("data-index",thisAppIndex);
        if(modal.css("right") == "-500px"){
        	showModal();
        }        
    });
    //modal close
    $(document).on("click",".close",function(){
        closeModal();
    });
    //升级
    
    $(".page-container").on("click",".upgrade",function(e){
    	e.stopPropagation();
        $("#upgradeModal").modal('show');
    });
}();*/
//账户信息修改
var extarFormValidate = function(){
	var userName = $("#userName").val(),
		contact = $("#contact").val(),
		mobileNum = $("#mobileNum").val(),
		corpName = $("#corpName").val(),
		province = $("#province").val(),
		city = $("#city").val(),
		area = $("#area").val(),
		industry = $("#industry").val();
	var validateReturn = true;
	if(userName == ""){
		$("body").msgBox({
			status:"error",
			time:1500,
	        msg:"账号不能为空，请重新输入"
	    })
	    validateReturn = false;
		return false;
	}else{
		if(!validateEmail(userName)){
			$("body").msgBox({
				status:"error",
				time:1500,
		        msg:"账号格式有误，请重新输入如：vyiyun@hr.com"
		    })
		    validateReturn = false;
			return false;
		}
	}
	if(contact==""){
		$("body").msgBox({
			status:"error",
			time:1500,
	        msg:"联系人不能为空，请重新输入"
	    })
	    validateReturn = false;
		return false;
	}
	if(mobileNum==""){
		$("body").msgBox({
			status:"error",
			time:1500,
	        msg:"手机号不能为空，请重新输入"
	    })
	    validateReturn = false;
		return false;
	}else{
		if(!validateTel(mobileNum)){
			$("body").msgBox({
				status:"error",
				time:1500,
		        msg:"手机号码有误，请重新输入"
		    })
		    validateReturn = false;
			return false;
		}
	}
	if(corpName==""){
		$("body").msgBox({
			status:"error",
			time:1500,
	        msg:"企业名称不能为空，请重新输入"
	    })
	    validateReturn = false;
		return false;
	}
	if(province=="请选择"||city=="请选择"||area=="请选择"){
		$("body").msgBox({
			status:"error",
			time:1500,
	        msg:"请选择企业所在地"
	    })
		validateReturn = false;
		return false;
		
	}
	if(industry==""){
		$("body").msgBox({
			status:"error",
			time:1500,
	        msg:"请选择所属行业"
	    })
		validateReturn = false;
		return false;
	}
	return validateReturn;
}
var extraProfile = function(options){
	var config = {};
	$.extend(config,options);
	//头像
	FileuploadManaged("upload.action",function(){
		$("body").msgBox({
			status:"success",
			time:1000,
	        msg:"修改头像成功"
	    });
		window.location.reload();
	});
	//所在地
	$("#address").citySelect({prov:config.province, city:config.city, dist:config.county});
	//$("#address").citySelect();
	//行业
	var industryData = arrIndustry,
		$industry = $("#industry"),
		optionArr = new Array();
	//$industry.empty();
	var oldIndustry = $industry.find("option:selected").text();
	$industry.empty();
	for(var i=0;i<industryData.length;i++){
		var item = industryData[i];
		if(item.text == oldIndustry){
			optionArr.push("<option value='"+item.value+"' selected>"+item.text+"</option>");	
		}else{			
			optionArr.push("<option value='"+item.value+"'>"+item.text+"</option>");	
		}
	}
	$industry.append(optionArr.join(""));
	//修改信息
    $('#Jprofile-info').on("click",".edit-btn",function(){
        var btn = $(this).find(".btn");
        var btnTxt = btn.html();
        var controls = $('#Jprofile-info').find(".controls");
        if(btnTxt == "修改"){
        	btn.html("保存");
            controls.each(function(){
                var controlsEdit = $(this).find("input[type='text']").size() ? $(this).find("input[type='text']") :  $(this).find("select");
                var controlsSpan = $(this).find(".text-inline");
                controlsSpan.hide();
                controlsEdit.show();
            });
        }else{
        	//验证表单
        	if(extarFormValidate()){
        		//提交
        		
        		var data = {
        				"userName":$.trim($("#userName").val()),
        				"contact":$.trim($("#contact").val()),
        				"mobileNum":$.trim($("#mobileNum").val()),
        				"corpName":$.trim($("#corpName").val()),
        				"location":{"province":$.trim($("#province").val()),"city":$.trim($("#city").val()),"county":$.trim($("#area").val()),},
        				"industry":$.trim($("#industry").find("option:selected").text())
        		};
        		ajaxServer("modUserInfo.action",JSON.stringify(data),function(){
        			$("body").msgBox({
            			status:"success",
            			time:1000,
            	        msg:"保存成功"
            	    });
            		btn.html("修改");
            		controls.each(function(){
                        var controlsEdit = $(this).find("input[type='text']").size() ? $(this).find("input[type='text']") :  $(this).find("select");
                        var controlsSpan = $(this).find(".text-inline");
                        var eidtVal = controlsEdit.is("select") ? controlsEdit.find("option:selected").text():controlsEdit.val();
                        controlsSpan.html(eidtVal);
                        if(controlsEdit.is(":visible")){
                            controlsSpan.show();
                            controlsEdit.hide();
                        }else{
                            controlsSpan.hide();
                            controlsEdit.show();
                        }
                    });
    	        });
        		
        		
        	}
        }
        
    });
    //修改密码
    $('#Jprofile-password').on("click",".edit-btn",function(){
        var btn = $(this).find(".btn");
        var btnTxt = btn.html();
        if(btnTxt == "修改"){
        	btn.html("保存")
        	$("#Jprofile-password").find("input[type='password']").val("");
        }else{
        	var psw1 = $("#Jprofile-password").find("input[type='password']:eq(0)").val();
        	var psw2 = $("#Jprofile-password").find("input[type='password']:eq(1)").val();
        	if(psw1 != psw2){
        		$("body").msgBox({
        			status:"error",
        			time:1500,
    		        msg:"您两次输入的密码不一致，请重新输入"
    		    })
    		    return false;
        	}else if(psw1.length < 6 || psw1.length > 16 ){
        		$("body").msgBox({
        			status:"error",
        			time:1500,
    		        msg:"密码长度应为6-16位，请重新输入"
    		    })
    		    return false;
        	}else{
        		btn.html("修改");
        		var newPsw = psw2;
    	        $("#Jprofile-password").find("input[type='hidden']").val(newPsw);
    	        ajaxServer("resetPwd.action",JSON.stringify({
    				"password":newPsw
    			}),function(){
    	        	$("body").msgBox({
            			status:"success",
            			time:1500,
        		        msg:"修改密码成功"
        		    })
    	        });
        	}
        }
        $('#Jprofile-password').find(".change-password").toggle();
    });
}
//验证邮箱
var validateEmail=function(email){
	var regEmail = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
	var isEmail = regEmail.test(email);
	return isEmail;
}
//验证手机号
var validateTel=function(tel){
	var regTel = /^1[\d]{10}$/;
	var isTel = regTel.test(tel);
	return isTel;
}

//请求数据
var ajaxServerOptions = {
        "isSending":false
};
var ajaxServer = function(url,data,callback,option){
	var defaultOption = {
            type : 'post',
            url : url,
            data : data || {},
            dataType : "json",
            beforeSend:function(){
            	ajaxServerOptions.isSending = true;
                    $("#loading").show();
    },
    complete:function(){
    	ajaxServerOptions.isSending = false;
		    $("#loading").hide();
	},
    error : function(err) {
    	ajaxServerOptions.isSending = false;
            $("#loading").hide();
            console.debug(err);
            $("body").msgBox({
		        msg:"提交失败，请稍后再试",
		        callMode:"func"
		    })
    },
    success : function(data) {
    	ajaxServerOptions.isSending = false;
            if (callback) {
                    callback(data);
            }
    }
	};
    if (option) {
            $.extend(defaultOption, option);
    }
    $.ajax(defaultOption);
	
}
//fileupload
var FileuploadManaged = function(url,callback){
	var url = url;
	function sendFile(file) {
        var uri = url;
        var xhr = new XMLHttpRequest();
        var fd = new FormData();
        
        xhr.open("POST", uri, true);
        xhr.onreadystatechange = function() {
            if (xhr.readyState == 4 && xhr.status == 200) {
                fileView("indexView.action",callback);
            }
        };
        fd.append('myFile', file);
        // Initiate a multipart/form-data upload
        xhr.send(fd);
    }
	function fileView(url,callback){
		var uri = "indexView.action";
        var xhr = new XMLHttpRequest();
        
        xhr.open("POST", uri, true);
        xhr.onreadystatechange = function() {
            if (xhr.readyState == 4 && xhr.status == 200) {
                // Handle response.
            	if(callback){
                	callback();
                }
                //alert(xhr.responseText);
            }
        };
        xhr.send(null);
	}
	$(".fileupload-Img-controls").on("change","input[type='file']",function(){
	    var file = this.files[0];
	    var reader = new FileReader(), htmlImage;
	    var previewArea = $(this).parents(".fileupload-Img-controls").find(".fileupload-preview");
	    reader.onload = function(e) {
	    	var avatar = e.target.result;
	        htmlImage = '<img src="'+ avatar +'" />';
	        previewArea.html(htmlImage);
	    }
	    sendFile(file);
	    reader.readAsDataURL(file);
	});
   
};
//invoice info edit tabs
var radioTabsManage = function(){
    return {
        init: function () {
            var isCk = $(".tab-invoice-info-radio").find("input[type='radio']:checked").parents(".radio").attr("data-tab");
            $(".tab-radio-content").hide();
            $(isCk).show();
            $(".tab-invoice-info-radio").on("click",".radio",function(e){
                if(e.target.type!="radio"){//防止label冒泡
                        e.stopPropagation();
                        var thisTab = $(this).attr("data-tab");
                        $(".tab-radio-content").hide();
                        $(thisTab).show();
                }
            });
        }
    }
}();

//操作提示框
+(function ($) {
	$.fn.msgBox = function(options){
		var settings = {
	        status: 'success',//success,info,error
	        msg: '成功',
	        istime:true,
	        time:3000,
	        end:function(){}
	    };
		 return this.each(function () { 
	        var opts =  $.extend(settings, options);
	        var sClass,status = opts.status,msg = opts.msg,t= opts.time;
            switch(status){
            	case "loading":
            		sClass = "alert-info";
            		msg = '<i class="icon-spinner icon-spin"></i>' + msg;
            		opts.istime = false;
            		break;
	            case "success":
	              opts.istime = true;
	              sClass = "alert-success";
	              break;
	            case "danger":
		              opts.istime = true;
		              sClass = "alert-danger";
		              break;
	            case "info":
	              opts.istime = true;
	              sClass = "alert-info";
	              break;
	            case "error":
	              opts.istime = true;
	              sClass = "alert-error";
	              break;
	            }
	            var msgHtml = '<div class="alert alert-modal '+sClass+'">'+msg+'</div>';
	            var backdrop = '<div class="modal-backdrop alert-modal-backdrop"></div>';
	            $body.append(msgHtml);
	            if($(".modal-backdrop").length==0){
	            	$body.append(backdrop);
	            }
	            if(opts.istime){
	            	setTimeout(function(){
	                    $(".alert-modal,.alert-modal-backdrop").remove();
	                    opts.end();
	                },t); 
	            }
		 });
		
	};
	$.msgBox = {
		close : function(){
			$(".alert-modal,.alert-modal-backdrop").remove();
		}
	};
})(window.jQuery);
//确认提示框
+(function ($) {
	 $.fn.modalBox = function(options){
	        var settings = {
	            title: '确认',//success,info,error
	            msg: '确认删除？',
	            okFun:function(){}
	        };
	        return this.each(function () { 
	        	var opts =  $.extend(settings, options);
	            var modal = '<div id="sureBox" class="modal hide fade">'+
	            '<div class="modal-header">'+
	              '<button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>'+
	              '<h3>'+opts.title+'</h3>'+
	           ' </div>'+
	            '<div class="modal-body">'+
	              '<p>'+opts.msg+'</p>'+
	            '</div>'+
	            '<div class="modal-footer">'+
	              '<a href="javascript:void(0)" class="cancel btn">取消</a>'+
	              '<a href="javascript:void(0)" class="sure btn orange">确认</a>'+
	            '</div></div>';
	            $("#sureBox").remove();
	    		$(modal).appendTo("body").modal("show");
	    		$("#sureBox").on("click",".cancel",function(event){
	    			$("#sureBox").modal("hide");
	    		});
	    		$("#sureBox").on("click",".sure",function(event){
	    			opts.okFun();
	    		});
	    		
	        });
	        
	    };
	    $.modalBox = {
			close : function(){
		    	$("#sureBox").modal("hide");
		    }	
	    };
})(window.jQuery);
$(function(){
    //权限管理-更改管理员
    $('#JadminAccount').on("click","#JchangeAdmin",function(){
        $('#JadminAccount').hide();
        $('#form-privilegeAdmin').find("input[type='password']").val("").parents(".control-group ").removeClass("success");
        $('#form-privilegeAdmin').show();
    });
    //权限管理员-停用
    $('#JadminAccount').on("click","#JAdminStatus",function(){
        var $this = $(this);
        var status = $this.attr("data-status");
        if(status == "1"){
            $("body").modalBox({
                title:"确认",
                msg:"确认停用此管理员？",
                okFun:function(){
                    $this.html("启用");
                }
            });
        }else{
            $("body").modalBox({
                title:"确认",
                msg:"确认启用此管理员？",
                okFun:function(){
                    $this.html("停用");
                }
            });
        }
        
    });
    //管理员
    if($('#adminTree').length){
    	$('#adminTree').jstree({
    		'core' : {
    			'data' : [
    			          {
    			        	  "text" : "管理组一",
    			        	  "state" : {"opened" : true },
    			        	  "children" : [
    			        	                {"text" : "管理员1","state" : { "selected" : true }},
    			        	                { "text" : "管理员21"}
    			        	                ]
    			          },
    			          {
    			        	  "text" : "管理组二",
    			        	  "state" : {"opened" : true },
    			        	  "children" : [
    			        	                {"text" : "管理员1"},
    			        	                { "text" : "管理员2"}
    			        	                ]
    			          }
    			          ],
    			          "check_callback" : true 
    		}
    	});
    }

});

//管理员关联人员
var adminSelect2 = function(){
        var userData = [{ id: 0, text: '刘闯闯' }, { id: 1, text: '郑之硕' }, { id: 2, text: '单道远' }, { id: 3, text: '李玉霞' }, { id: 4, text: '王芹' }];
		if($("#admin_select2").length){
			$("#admin_select2").select2({
				placeholder: "请选择关联人员",
				data: userData,
				allowClear: true
			});
		}
}();