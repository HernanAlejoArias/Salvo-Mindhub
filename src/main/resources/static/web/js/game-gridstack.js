function updateGameGrid(ships) {
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
        animate: true,
        resizable: false
    }
    //se inicializa el grid con las opciones
    $('.grid-stack').gridstack(options);
    grid = $('#grid').data('gridstack');

    for(var i = 0; i < ships.length; i++ ){
        grid.addWidget(ships[i].el, ships[i].x, ships[i].y, ships[i].width, ships[i].height);
    }

    grid.resizable('.grid-stack-item', false);

    $(".grid-stack-item").dblclick(function(){
        var shipContainer = $(this); 
        var selectedShip = $(this).find(".grid-stack-item-content")

        var x = parseInt(shipContainer.attr("data-gs-x"));
        var y = parseInt(shipContainer.attr("data-gs-y"));
        var newHeight = parseInt(shipContainer.attr("data-gs-width"));
        var newWidth = parseInt(shipContainer.attr("data-gs-height"));
        
        var willFit = willItFit(x, y, newWidth, newHeight);

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

}

function willItFit(x, y, width, height){
    if ((x + width) > 10){
        return false
    }
    if((y + height) > 10){
        return false
    }
    return true;
}

function removeShipsFromGrid(){
    grid = $('#grid').data('gridstack');
    grid.removeAll(true)
};