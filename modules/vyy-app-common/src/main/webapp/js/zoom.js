
(function($) {
	$('body').append('<div id="zoom"><a class="close"></a><a href="#previous" class="previous"></a><a href="#next" class="next"></a><div class="content loading"></div></div>');
	var zoom = $('#zoom').hide(),
    zoomContent = $('#zoom .content'),
    zoomedIn = false,
    openedImage = null,
    windowWidth = $(window).width(),
    windowHeight = $(window).height();
	var settings = {
		boxName : 'li',
		parentName : ".zoom-list",
        tagName : 'a'
	};
	
	var $boxParent = $(settings.parentName);
	
	$.zoom = {	
	}	
	$.zoom.close = function(event){
		if (event)
			event.preventDefault();
		zoomedIn = false;
		openedImage = null;
		zoom.hide();
		$('body').removeClass('zoomed');
		zoomContent.empty();
	}
	
	$.zoom.bindEvent = function(){
		//事件绑定		
		$(settings.parentName).delegate(settings.tagName, "click",$.zoom.open);
	}
	$.zoom.init = function(options) {     
		var that = this;
		if (options) {
			settings = $.extend(settings, options);  
		}
		
		$(settings.parentName).delegate(settings.tagName, "click",$.zoom.open);
		//杂项
		function openPrevious() {
			var prev = openedImage.parents(settings.boxName).prev();
			if (prev.length == 0)
				prev = $boxParent.find(settings.boxName).last(); 
			//$('.upload_preview .upload_append_list:last-child');
			prev.find('a').trigger('click');
		}
	
		function openNext() {
			var next = openedImage.parents(settings.boxName).next();
			if (next.length == 0)
				next = $boxParent.find(settings.boxName).first()
			//$('.upload_preview .upload_append_list:first-child');
			next.find('a').trigger('click');
		}
			
//		function close(event) {
//			
//		}
		
		function changeImageDimensions() {
			windowWidth = $(window).width();
			windowHeight = $(window).height();
		}
		
		(function bindNavigation() {
			zoom.on('click', function(event) {
				event.preventDefault();
				if ($(event.target).attr('id') == 'zoom')
					$.zoom.close();
			});
			
			$('#zoom .close').on('click', $.zoom.close);
			$('#zoom .previous').on('click', openPrevious);
			$('#zoom .next').on('click', openNext);
			$(document).keydown(function(event) {
				if (!openedImage) return;
				if (event.which == 38 || event.which == 40)
					event.preventDefault();
				if (event.which == 27)
					$.zoom.close();
				if (event.which == 37 && !openedImage.hasClass('zoom'))
					openPrevious();
				if (event.which == 39 && !openedImage.hasClass('zoom'))
					openNext();
			});
		
			if ($boxParent.find(settings.tagName).length == 1)
				$boxParent.find(settings.tagName).eq(0).addClass('zoom');
			$('.zoom').on('click', $.zoom.open);
			
			
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
		
		return that;
	}
	
	$.zoom.open = function(event){
		
		if (event){
			event.preventDefault();
			event.stopPropagation();
		}
		var link = $(this),
		    src = link.attr('data-href');
		if (!src)
			return;
		var image = $(new Image()).hide();
		$('#zoom .previous, #zoom .next').show();
		if (link.hasClass('zoom'))
			$('#zoom .previous, #zoom .next').hide();
		if (!zoomedIn) {
			zoomedIn = true;
			zoom.show();
			$('body').addClass('zoomed');
		}
		zoomContent.html(image).delay(500).addClass('loading');
		image.load(render).attr('src', src);
		openedImage = link;
		
		function render() {
			var image = $(this),
			    borderWidth = parseInt(zoomContent.css('borderLeftWidth')),
			    maxImageWidth = windowWidth - (borderWidth * 2),
			    maxImageHeight = windowHeight - (borderWidth * 2),
			    imageWidth = image.width(),
			    imageHeight = image.height();
			if (imageWidth == zoomContent.width() && imageWidth <= maxImageWidth && imageHeight == zoomContent.height() && imageHeight <= maxImageHeight) {
					show(image);
					return;
			}
			if (imageWidth > maxImageWidth || imageHeight > maxImageHeight) {
				var desiredHeight = maxImageHeight < imageHeight ? maxImageHeight : imageHeight,
				    desiredWidth  = maxImageWidth  < imageWidth  ? maxImageWidth  : imageWidth;
				if ( desiredHeight / imageHeight <= desiredWidth / imageWidth ) {
					image.width(imageWidth * desiredHeight / imageHeight);
					image.height(desiredHeight);
				} else {
					image.width(desiredWidth);
					image.height(imageHeight * desiredWidth / imageWidth);
				}
			}
			zoomContent.animate({
				width: image.width(),
				height: image.height(),
				marginTop: -(image.height() / 2) - borderWidth,
				marginLeft: -(image.width() / 2) - borderWidth
			}, 200, function() {
				show(image);
			});

			function show(image) {
				image.show();
				zoomContent.removeClass('loading');
			}
		}
	}
})(jQuery);

