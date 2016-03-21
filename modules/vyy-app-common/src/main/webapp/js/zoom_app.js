(function($) {
    var windowWidth = $(window).width(),
    windowHeight = $(window).height();
    $.fn.zoom = function(options){
        var settings = {
            boxName : 'li',
            parentName : ".zoom-list",
            tagName : 'a'
        };
        return this.each(function () {
            if (options) {
                settings = $.extend(settings, options);
            }
            var $this = $(this),
                _className = settings.parentName.replace(".","")+"-zoom";
            var $zoombox = $('<div class="zoom '+_className+'"><a class="close"></a><a href="#previous" class="previous"></a><a href="#next" class="next"></a><div class="content load"></div></div>');
            if($("."+_className).length === 0){
                $this.append($zoombox);
            }
            $zoombox.hide();
            var  zoomContent = $zoombox.find('.content');
            var zoomedIn = false,
                openedImage = null;
            var $boxParent = $(settings.parentName);
            $(settings.parentName).delegate(settings.tagName, "click",open);
            //杂项
            function openPrevious() {
                var prev = openedImage.parents(settings.boxName).prev(settings.boxName);
                if (prev.length == 0)
                    prev = $boxParent.find(settings.boxName).last(); 
                //$('.upload_preview .upload_append_list:last-child');
                prev.find(settings.tagName).trigger('click');
            }
        
            function openNext() {
                var next = openedImage.parents(settings.boxName).next(settings.boxName);
                if (next.length == 0)
                    next = $boxParent.find(settings.boxName).first();
                //$('.upload_preview .upload_append_list:first-child');
                next.find(settings.tagName).trigger('click');
            }
            
            function changeImageDimensions() {
                windowWidth = $(window).width();
                windowHeight = $(window).height();
            }
            function open(event){
                if (event){
                    event.preventDefault();
                    event.stopPropagation();
                }
                var link = $(this),
                    src = link.attr('data-original');
                if (!src)
                    return;
                var image = $(new Image()).hide();
                $zoombox.find('.previous').show();
                $zoombox.find('.next').show();
                if (link.hasClass('zoom-only')){
                    $zoombox.find('.previous').hide();
                    $zoombox.find('.next').hide();
                }

                if (!zoomedIn) {
                    zoomedIn = true;
                    $zoombox.show();
                    $('body').addClass('zoomed');
                }
                zoomContent.html(image).delay(500).addClass('load');
                image.load(render).attr('src', src);
                openedImage = link;
                function render() {
                    var $image = $(this),
                        borderWidth = parseInt(zoomContent.css('borderLeftWidth')),
                        maxImageWidth = windowWidth - (borderWidth * 2),
                        maxImageHeight = windowHeight - (borderWidth * 2),
                        imageWidth = $image[0].width,
                        imageHeight = $image[0].height;
                    if (imageWidth == zoomContent.width() && imageWidth <= maxImageWidth && imageHeight == zoomContent.height() && imageHeight <= maxImageHeight) {
                        show($image);
                        return;
                    }
                    if (imageWidth > maxImageWidth || imageHeight > maxImageHeight) {
                        var desiredHeight = maxImageHeight < imageHeight ? maxImageHeight : imageHeight,
                            desiredWidth  = maxImageWidth  < imageWidth  ? maxImageWidth  : imageWidth;
                        if ( desiredHeight / imageHeight <= desiredWidth / imageWidth ) {
                            $image.width(imageWidth * desiredHeight / imageHeight);
                            $image.height(desiredHeight);
                        } else {
                            $image.width(desiredWidth);
                            $image.height(imageHeight * desiredWidth / imageWidth);
                        }
                    }
                    zoomContent.animate({
                        width: $image.width(),
                        height: $image.height(),
                        marginTop: -($image.height() / 2) - borderWidth,
                        marginLeft: -($image.width() / 2) - borderWidth
                    }, 300, function() {
                        show($image);
                    });

                    function show(image) {
                        image.show();
                        zoomContent.removeClass('load');
                    }
                }
            }
            (function bindNavigation() {
                $zoombox.on('click', function(event) {
                    event.preventDefault();
                    if ($(event.target).attr('class').indexOf('zoom')>=0)
                        close();
                });

                $zoombox.find('.close').on('click', close);
                $zoombox.find('.previous').on('click', openPrevious);
                $zoombox.find('.next').on('click', openNext);
                $(document).keydown(function(event) {
                    if (!openedImage) return;
                    if (event.which == 38 || event.which == 40)
                        event.preventDefault();
                    if (event.which == 27)
                       close();
                    if (event.which == 37 && !openedImage.hasClass('zoom-only'))
                        openPrevious();
                    if (event.which == 39 && !openedImage.hasClass('zoom-only'))
                        openNext();
                });
            
                if ($boxParent.find(settings.tagName).length == 1){
	               	 $boxParent.find(settings.tagName).eq(0).addClass('zoom-only');
	            }else{
	               	$boxParent.find('.zoom-only').removeClass("zoom-only");
	            }
               // $.on('click', open);
            })();
            
            (function bindChangeImageDimensions() {
                $(window).on('resize', changeImageDimensions);
            })();
        
            (function bindScrollControl() {
                $(window).on('mousewheel DOMMouseScroll', function(event) {
                    if (!openedImage)
                        return;
                    event.stopPropagation();
                    event.preventDefault();
                    event.cancelBubble = false;
                });
            })();
            function  close(event){
                if (event)
                    event.preventDefault();
                zoomedIn = false;
                openedImage = null;
                $zoombox.hide();
                $('body').removeClass('zoomed');
                zoomContent.empty();
            }
            $(settings.parentName).delegate(settings.tagName, "click",open);
        });
    };
})(jQuery);