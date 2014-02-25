var carouselIndex = 0;
function navigation(listid, navigationid) {
    // To use GateIn's jquery library
    require(["SHARED/jquery"], function($) {
        var list = "#" + listid;
        var nav = "#" + navigationid;
        var left = "#topnews_nav_left";
        var right= "#topnews_nav_right";
        var size = $(list + " li").size();
        $(nav).append('<ol>').addClass('mockup-nav-list');
        for (var i=0; i<size; i++) {
            $(nav + " ol").append('<li>');
            if (i==0) {
                $(nav + " li").addClass('mockup-active');
            }
        }
        $(nav).delegate('li', 'click', function () {
            var index = $(this).index();
            carouselIndex = index;
            for (var i=0; i<size; i++) {
                var li = $(list + " li").eq(i);
                var linav = $(nav + " li").eq(i);
                if (index == i) {
                    li.css({'display':'block'});
                    linav.addClass('mockup-active');
                } else {
                    li.css({'display':'none'});
                    linav.removeClass('mockup-active');
                }
            }
        });
        $(left).on('click', function() {
            if (carouselIndex > 0) {
                carouselIndex--;
                for (var i=0; i<size; i++) {
                    var li = $(list + " li").eq(i);
                    var linav = $(nav + " li").eq(i);
                    if (carouselIndex == i) {
                        li.css({'display':'block'});
                        linav.addClass('mockup-active');
                    } else {
                        li.css({'display':'none'});
                        linav.removeClass('mockup-active');
                    }
                }
            }
        });
        $(right).on('click', function() {
            if (carouselIndex < ($(list + " li").size() - 1)) {
                carouselIndex++;
                for (var i=0; i<size; i++) {
                    var li = $(list + " li").eq(i);
                    var linav = $(nav + " li").eq(i);
                    if (carouselIndex == i) {
                        li.css({'display':'block'});
                        linav.addClass('mockup-active');
                    } else {
                        li.css({'display':'none'});
                        linav.removeClass('mockup-active');
                    }
                }
            }
        });
    });
}