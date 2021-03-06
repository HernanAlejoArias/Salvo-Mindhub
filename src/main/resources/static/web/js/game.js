$(document).ready(function () {
	console.log(".Ready!");

	setListeners();

	if ($("#running-games").length > 0) {
		createGamesPage();
	}

	if ($("#game-grid").length > 0) {

		// Crea la grilla de Salvoes vacia
		createGameGrids();
	}
})

function saveShipsLocations() {
	var ships = [];

	$(".grid-stack-item").each(function () {
		var type = $(this).attr("id");
		var locations = [];

		if (parseInt($(this).data("gs-width")) > 1) {
			for (var i = 0; i < $(this).data("gs-width"); i++) {
				var x = parseInt(($(this).data("gs-x")) + i) + 1;
				var y = parseInt(($(this).data("gs-y")) + 65);

				locations.push(String.fromCharCode(y) + x);
			}
		} else {
			for (var i = 0; i < $(this).data("gs-height"); i++) {
				var x = parseInt(($(this).data("gs-x")) + 1);
				var y = parseInt(($(this).data("gs-y")) + i) + 65;

				locations.push(String.fromCharCode(y) + x);
			}
		}
		// 		

		var ship = {
			type: type,
			locations: locations
		}

		ships.push(ship);
	})

	return ships;
};

function createGamesPage() {
	$("#running-games ol").empty();
	$("#leader-board").empty();

	$.get("http://localhost:8080/api/games", function (responseData) {
		console.log("api/games -> .get")
	}).done(function (responseData) {
		createLeaderBoard(responseData.games);
		createListOfGames(responseData);
		showLogInLogOut(responseData.player);
		showWellcomeUser(responseData.player);
	});
}

function correctEmailFormat(email) {
	var RegExp = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/

	return RegExp.test(email);
}

function showWellcomeUser(player) {
	if (player == null) {
		$("#user-name").parent().toggle(false);
		$("#btn-new-game").toggle(false);
	} else {
		$("#user-name").parent().toggle(true);
		$("#user-name").text(player.email);
		$("#btn-new-game").toggle(true);
	}
};

function showLogInLogOut(player) {
	if (player == null) {
		$("#btn-log-out").toggle(false);
		$("#btn-log-in").toggle(true);
	} else {
		$("#btn-log-out").toggle(true);
		$("#btn-log-in").toggle(false);
	}
}

function logInUser(email, password) {
	$.post("/api/login", {
			username: email,
			password: password
		})
		.done(function () {
			$(".modal").modal('toggle');
			createGamesPage();
		})
		.fail(function () {
			$("#emailHelp").removeClass("text-muted");
			$("#emailHelp").addClass("text-danger");
			$("#emailHelp").text("Please check the User and Password");
		})
}

function SignUpUser(email, password) {
	$.post("/api/players", {
			username: email,
			password: password
		})
		.done(function () {
			logInUser(email, password);
		})
		.fail(function (data) {
			$("#emailHelp").removeClass("text-muted");
			$("#emailHelp").addClass("text-danger");
			$("#emailHelp").text(data.responseText);
		})
}

function logOutUser() {
	$.post("/api/logout")
		.done(function () {
			location.replace("/web/games.html");
			createGamesPage();
		})
}

function Player(id, userName, score) {
	this.id = id;
	this.userName = userName;
	this.total = 0;
	this.wins = 0;
	this.loses = 0;
	this.ties = 0;
	this.setTotal = function (score) {
		if (score != undefined) {
			this.total += score;
		}
	}
	this.setResults = function (score) {
		if (score != undefined) {
			if (score === 0) {
				this.loses++;
			} else if (score === 1) {
				this.wins++;
			} else {
				this.ties++;
			}
		}
	}
}

function createLeaderBoard(responseData) {
	var players = [];

	$.each(responseData, function (index, data) {
		$.each(data.gamePlayers, function (index, gamePlayer) {

			var player = players.find(function (element) {
				return element.id === gamePlayer.player.id
			});

			if (player == undefined) {
				var player = new Player(gamePlayer.player.id, gamePlayer.player.email, gamePlayer.player.score)
				player.setTotal(gamePlayer.score);
				player.setResults(gamePlayer.score);
				players.push(player);
			} else {
				player.setTotal(gamePlayer.score);
				player.setResults(gamePlayer.score);
			}
		})
	})

	$.each(players, function (index, player) {
		el_li_tr = $(document.createElement("tr"));
		// UserName
		el_li_td = $(document.createElement("td"));
		el_li_td.text(player.userName);
		el_li_tr.append(el_li_td);
		// Total
		el_li_td = $(document.createElement("td"));
		el_li_td.text(player.total);
		el_li_tr.append(el_li_td);
		// Wins
		el_li_td = $(document.createElement("td"));
		el_li_td.text(player.wins);
		el_li_tr.append(el_li_td);
		// Loses
		el_li_td = $(document.createElement("td"));
		el_li_td.text(player.loses);
		el_li_tr.append(el_li_td);
		// Ties
		el_li_td = $(document.createElement("td"));
		el_li_td.text(player.ties);
		el_li_tr.append(el_li_td);
		$("#leader-board").append(el_li_tr);
	})
}

function placeSalvos(salvoes, ships) {
	$.each(salvoes, function (index, salvo) {
		if (salvo.player == playerOne.id) {
			placeSalvoOnSalvosGrid(salvo.turn, salvo.locations)
		} else {
			placeSalvoOnGameGrid( salvo.locations, ships)
		}
	})
}

function placeSalvoOnSalvosGrid(turn, locations) {

	var el_grid = $("#salvoes-grid");

	$.each(locations, function (index, location) {
		el_salvo = el_grid.find("#" + location).text(turn);
		el_salvo.removeClass("can-shot");
		el_salvo.removeClass("shooted");
		if (el_salvo.data("shipOnCell") == true) {
			el_salvo.addClass("hit");
		} else {
			el_salvo.addClass("salvo");
		}
	})
}

function placeSalvoOnGameGrid(locations, ships) {

	el_grid = $("#game-grid");

	$.each(locations, function (index, location) {

		var x = (location.slice(1, 2)) - 1;
		var y = (location.slice(0, 1).charCodeAt(0) - 65);
	
		var fromTop = y * 50;
		var fromLeft = x * 50;
		var cellId = location.slice(0, 1) + (location.slice(1, 2));

		$("#grid").append($("<div id=" + cellId + " class='game-cell salvo' style='position: absolute; top:" + fromTop + "px; left: "+ fromLeft +"px;'></div>").data("shipOnCell", true));

	})

	$.each(ships, function (index, ship) {

		for(var i = 0; i < ship.locations.length; i++){
			if ($("#grid #"+ship.locations[i]).data("shipOnCell") == true) {
				$("#grid #"+ship.locations[i]).removeClass("salvo");
				$("#grid #"+ship.locations[i]).addClass("hit");
			} else {
				$("#grid #"+ship.locations[i]).addClass("salvo");
			}	
		}
	})
}

function playersData(gamePlayers, gamePlayerID) {
	$.each(gamePlayers, function (index, player) {
		if (gamePlayerID == player.gamePlayerID) {
			$("#P1").text(player.player.email);
			playerOne = player.player;
		} else {
			$("#P2").text(player.player.email);
		}
	});
}

function placeShips(ships) {
	var shipsOnGrid = [];
	var staticGridOption;

	if (ships.length === 0) {
		staticGridOption = false;
		var carrier = {
			el: $('<div id="carrier"><div class="grid-stack-item-content carrier"></div><div/>'),
			x: 0,
			y: 0,
			width: 5,
			height: 1,
			autoPosition: false
		}

		shipsOnGrid.push(carrier);

		var patrol = {
			el: $('<div id="patrol"><div class="grid-stack-item-content patrol"></div><div/>'),
			x: 0,
			y: 1,
			width: 2,
			height: 1,
			autoPosition: false
		}

		shipsOnGrid.push(patrol);

		var submarine = {
			el: $('<div id="submarine"><div class="grid-stack-item-content submarine"></div><div/>'),
			x: 0,
			y: 2,
			width: 3,
			height: 1,
			autoPosition: false
		}

		shipsOnGrid.push(submarine);

		var destroyer = {
			el: $('<div id="destroyer"><div class="grid-stack-item-content destroyer"></div><div/>'),
			x: 0,
			y: 3,
			width: 3,
			height: 1,
			autoPosition: false
		}

		shipsOnGrid.push(destroyer);

		var battleship = {
			el: $('<div id="battleship"><div class="grid-stack-item-content battleship"></div><div/>'),
			x: 0,
			y: 4,
			width: 4,
			height: 1,
			autoPosition: false
		}

		shipsOnGrid.push(battleship);

	} else {
		staticGridOption = true;

		$("#save-ships").toggle(false);
		for (var i = 0; i < ships.length; i++) {
			var isHorizontal;

			if (ships[i].locations[0].slice(0, 1) === ships[i].locations[1].slice(0, 1)) {
				isHorizontal = true
			} else {
				isHorizontal = false
			}

			var x = (ships[i].locations[0].slice(1, 2)) - 1;
			var y = (ships[i].locations[0].slice(0, 1).charCodeAt(0) - 65);
			var width;
			var height;
			var shipType;

			if (isHorizontal) {
				width = ships[i].locations.length;
				height = 1;
				shipType = ships[i].type;
			} else {
				width = 1;
				height = ships[i].locations.length;
				shipType = ships[i].type + " vertical";
			}

			var ship = {
				el: $('<div id="' + ships[i].type + '"><div style="z-index: -2" class="grid-stack-item-content ' + shipType + '"></div><div/>'),
				x: x,
				y: y,
				width: width,
				height: height,
				autoPosition: false
			}
			shipsOnGrid.push(ship);
		}

		$(".grid-stack-item").unbind("dblclick")
	}

	updateGameGrid(shipsOnGrid, staticGridOption);
//	$("#game-grid").off("dblclick", ".grid-stack-item");
	return shipsOnGrid;
}

function createGameGrids() {
	createGrid($("#salvoes-grid"))

	let gamePlayerID = paramObj(window.location.search).gp;
	let apiCallURL = "http://localhost:8080/api/game_view/" + gamePlayerID;
	let playerOne;

	$.get(apiCallURL, function (responseData) {
		console.log("api/game_view -> .get")
	}).done(function (responseData) {
		var shipsOnGrid = placeShips(responseData.ships);
		playersData(responseData.gamePlayers, gamePlayerID);
		placeSalvos(responseData.salvoes, responseData.ships);
		administrateTurns(responseData.ships, responseData.salvoes)
	});
}

function createGrid(el_grid) {

	if (el_grid.children().length === 0){
		// Filas
		for (let i = 64; i < 76; i++) {
			el_li_tr = $(document.createElement("tr"));
			el_grid.append(el_li_tr);
			for (let j = 0; j < 11; j++) {
				el_li_td = $(document.createElement("td"));
				if (i == 64 && j == 0) {
					el_li_tr.append(el_li_td.text(""));
				} else if (i == 64) {
					el_li_tr.append(el_li_td.text(j));
				} else if (j == 0) {
					el_li_tr.append(el_li_td.text(String.fromCharCode(i)));
				} else {
					el_li_tr.append(el_li_td.text(""));
					el_li_td.attr("id", String.fromCharCode(i) + j);
					el_li_td.addClass("can-shot");
				}
			}
		}
	}
}

function createListOfGames(responseData) {
	var lista = responseData.games.map(
		function (game) {
			var el_li_game = $(document.createElement("li")).text(new Date(game.created).toLocaleString());
			var el_ol_player = $(document.createElement("ol"));
			var el_lis_player = game.gamePlayers.map(function (player) {
				return $(document.createElement("li")).text(player.player.email);
			});

			el_ol_player.append(el_lis_player);
			el_li_game.append(el_ol_player);

			if (responseData.player != null) {
				var el_btn_enter_game = $(document.createElement("button")).addClass("btn btn-outline-secondary btn-enter-game").text("Enter Game");
				var el_btn_join_game = $(document.createElement("button")).addClass("btn btn-outline-primary btn-join-game").text("Join Game");

				let loggedPlayer = game.gamePlayers.find(function (element) {
					return element.player.id === responseData.player.id
				});

				if (loggedPlayer) {
					el_btn_game = $(document.createElement("a")).attr("href", "/web/game.html?gp=" + loggedPlayer.id).append(el_btn_enter_game);
				} else {
					el_btn_game = el_btn_join_game
				}

				el_btn_game.data("gameId", game.id);
				el_li_game.append(el_btn_game);
			}

			return el_li_game;
		}
	);
	$("ol").append(lista);
}

function paramObj(search) {
	var obj = {};
	var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

	search.replace(reg, function (match, param, val) {
		obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
	});

	return obj;
}

function createNewGame() {
	$.post("/api/games")
		.done(function (responseData) {
			location.href = "/web/game.html?gp=" + responseData.gpid;
		})
		.fail(function (responseData) {
			alert("Error on the Game Creation: " + responseData.responseText)
		})

};

function joinGame(gameId) {
	var apiUrl = "/api/game/" + gameId + "/players";

	$.post(apiUrl)
		.done(function (responseData) {
			location.href = "/web/game.html?gp=" + responseData.gpid;
		})
		.fail(function (responseData) {
			alert("Error Joinning the Game: " + responseData.responseText)
		})
};

function setListeners() {

	$("#running-games").on("click", "#btn-new-game", function () {
		createNewGame();
	})

	$("#running-games").on("click", ".btn-join-game", function () {
		var joinGameId = $(this).data("gameId");

		joinGame(joinGameId);
	})

	$(".bote").click(function () {
		if ($(this).hasClass("vertical")) {
			$(this).removeClass("vertical");
		} else {
			$(this).addClass("vertical");
		}
	})

	$("#btn-log-in").click(function () {
		$("#emailHelp").addClass("text-muted");
		$("#emailHelp").removeClass("text-danger");
		$("#emailHelp").text("Your email will be used as your User Name");
		$("#inputEmail").val("");
		$("#inputPassword").val("");
	})

	$("#btn-login").click(function () {
		if (correctEmailFormat($("#inputEmail").val())) {
			logInUser($("#inputEmail").val(), $("#inputPassword").val());
		} else {
			$("#emailHelp").text("Invalid email");
			$("#emailHelp").removeClass("text-muted");
			$("#emailHelp").addClass("text-danger");
		}
	});

	$("#btn-log-out").click(function () {
		logOutUser();
	});

	$("#btn-signup").click(function () {

		if ($("#inputEmail").val() == "" || $("#inputPassword").val() == "") {
			alert("Complete the User and Password")
		} else if (!correctEmailFormat($("#inputEmail").val())) {
			$("#emailHelp").text("Invalid email");
			$("#emailHelp").removeClass("text-muted");
			$("#emailHelp").addClass("text-danger");
		} else {
			SignUpUser($("#inputEmail").val(), $("#inputPassword").val());
		}
	});

	$("#save-ships").click(function () {
		addShipsToGame()
	})

	$("#salvoes-grid").on("click", ".can-shot", function () {
		if($("#salvoes-grid .shooted").length < 5){
			$(this).removeClass("can-shot");
			$(this).addClass("shooted");	
		}else {
			alert("you can only shot " + 5);
		}
	});

	$("#salvoes-grid").on("click", ".shooted", function () {
		$(this).removeClass("shooted");
		$(this).addClass("can-shot");
	});

	$("#btn-shot").on("click",function(){ 
		shotSalvoes($(this).data("turn-nro"));
		removeShipsFromGrid();
		createGameGrids();
	});
}

function addShipsToGame(gamePlayerID) {
	if (!gamePlayerID) {
		var gamePlayerID = paramObj(window.location.search).gp;
	}
	var apiUrl = "/api/games/players/" + gamePlayerID + "/ships";


	var ships = saveShipsLocations();

	$.post({
			url: apiUrl,
			data: JSON.stringify(ships),
			dataType: "text",
			contentType: "application/json"
		})
		.done(function (responseData) {
			$(".modal").modal('toggle');
			removeShipsFromGrid();
			createGameGrids();
		})
		.fail(function (responseData) {
			alert("Error Addind Ships: " + responseData.responseText)
		})
}

function shotSalvoes(turn) {

	if (!gamePlayerID) {
		var gamePlayerID = paramObj(window.location.search).gp;
	}
	var apiUrl = "/api/games/players/" + gamePlayerID + "/salvos";

	$.post({
			url: apiUrl,
			data: JSON.stringify({
				turn: turn,
				locations: getShootedSalvos()
			}),
			dataType: "text",
			contentType: "application/json"
		})
		.done(function (responseData) {
			console.log("Salvos Shooted: " + responseData.responseText)
		})
		.fail(function (responseData) {
			alert("Error Addind Salvos: " + responseData.responseText)
		})
}

function administrateTurns(ships, salvoes){
	if (ships.length > 0){
		$("#btn-shot").toggle(true).data("turn-nro",salvoes.length + 1 );
	} else {
		$("#btn-shot").toggle(false);
	}
}

function getShootedSalvos(){
	var salvos = [];

	if ($(".shooted").length > 0){
		$(".shooted").each(function(){
			salvos.push($(this).attr("id"));
		})
	}else{
		alert("No salvos were setted")
	}

	return salvos;
}