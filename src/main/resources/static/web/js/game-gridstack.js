$(function () {
    var options = {
        //grilla de 10 x 10
        width: 10,
        height: 10,
        //separacion entre elementos (les llaman widgets)
        verticalMargin: 0,
        //altura de las celdas
        cellHeight: 50,
        //desabilitando el resize de los widgets
        disableRsesize: true,
        //widgets flotantes
        float: true,
        //removeTimeout: 100,
        //permite que el widget ocupe mas de una columna
        disableOneColumnMode: true,
        //false permite mover, true impide
        staticGrid: false,
        //activa animaciones (cuando se suelta el elemento se ve más suave la caida)
        animate: true
    }
    //se inicializa el grid con las opciones
    $('.grid-stack').gridstack(options);
    grid = $('#grid').data('gridstack');

    /* 			//agregando un elmento(widget) desde el javascript
        grid.addWidget($('<div id="submarine"><div class="grid-stack-item-content submarineHorizontal"></div><div/>'),
            1, 5, 3, 1, false, 1, 3, 1, 3, "submarine");

        grid.addWidget($('<div id="destroyer"><div class="grid-stack-item-content destroyerHorizontal"></div><div/>'),
            1, 8, 3, 1, false, 1, 3, 1, 3, "destroyer"); */

    //verificando si un area se encuentra libre
    //no está libre, false
    //console.log(grid.isAreaEmpty(1, 8, 3, 1));
    //está libre, true
    //console.log(grid.isAreaEmpty(1, 7, 3, 1));
    //todas las funciones se encuentran en la documentación
    //https://github.com/gridstack/gridstack.js/tree/develop/doc

    grid.resizable('.grid-stack-item', false);

    $(".grid-stack-item").dblclick(function(){
        var shipContainer = $(this); 
        var selectedShip = $(this).find(".grid-stack-item-content")

        var x = parseInt(shipContainer.attr("data-gs-x"));
        var y = parseInt(shipContainer.attr("data-gs-y"));
        var newHeight = parseInt(shipContainer.attr("data-gs-width"));
        var newWidth = parseInt(shipContainer.attr("data-gs-height"));

        var willFit = grid.willItFit(x, y, newWidth, newHeight, false);

        if(selectedShip.hasClass("vertical")){
            areaEmpty = grid.isAreaEmpty(x + 1, y, newWidth - 1, newHeight)
        }else{
            areaEmpty = grid.isAreaEmpty(x, y + 1, newWidth, newHeight - 1)
        }

        if (willFit && areaEmpty){
            grid.resize(shipContainer, newWidth, newHeight)
        
            if(selectedShip.hasClass("vertical")){
                selectedShip.removeClass("vertical")
            }
            else {
                selectedShip.addClass("vertical");
            }    
        }
        else {
            alert("Caution with Grid boundaries and Ship's overlapping")
        }
    })

});