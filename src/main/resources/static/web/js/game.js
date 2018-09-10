$(document).ready(function () {
	console.log(".Ready!");

	$("#btn-log-in").click(function(){
		$("#emailHelp").addClass("text-muted");
		$("#emailHelp").removeClass("text-danger");
		$("#emailHelp").text("Your email will be used as your User Name");
		$("#inputEmail").val("");
		$("#inputPassword").val("");
	})

	$("#btn-login").click(function(){
		if (correctEmailFormat($("#inputEmail").val())){
			logInUser($("#inputEmail").val(), $("#inputPassword").val());
		}else{
			$("#emailHelp").text("Invalid email");
			$("#emailHelp").removeClass("text-muted");
			$("#emailHelp").addClass("text-danger");
		}
	});

	$("#btn-log-out").click(function(){
		logOutUser();
	});

	$("#btn-signup").click(function(){

		if ($("#inputEmail").val() == "" || $("#inputPassword").val() == ""){
			alert("Complete the User and Password")
		}else if (correctEmailFormat($("#inputEmail").val())) {
			$("#emailHelp").text("Invalid email");
			$("#emailHelp").removeClass("text-muted");
			$("#emailHelp").addClass("text-danger");
		}
		else{
			SignUpUser($("#inputEmail").val(), $("#inputPassword").val());
		}
	});
	
	if ($("#running-games").length > 0) {
		createGamesPage();
	}

	if ($("#game-grid").length > 0) {

		createGameGrids();

		let gamePlayerID = paramObj(window.location.search).gp;
		let apiCallURL = "http://localhost:8080/api/game_view/" + gamePlayerID;
		let playerOne;

		$.get(apiCallURL, function (responseData) {
			console.log("api/game_view -> .get")
		}).done(function (responseData) {
			placeShips(responseData.ships);
			playersData(responseData.gamePlayers, gamePlayerID);
			placeSalvos(responseData.salvoes);
		});
	}

	$("#save-ships").click(function(){
		saveShipsLocations();
	})

})

function saveShipsLocations(){
	var ships = [];

	$(".grid-stack-item").each(function(){
		var type = $(this).attr("id");
		var locations = [];

		if (parseInt($(this).data("gs-width")) > 1){
			for (var i = 0; i < $(this).data("gs-width"); i++){
				var x = parseInt(($(this).data("gs-x")) + i ) + 1;
				var y = parseInt(($(this).data("gs-y")) + 65);

				locations.push( String.fromCharCode(y) + x );
			}
		} else {
			for (var i = 0; i < $(this).data("gs-height"); i++){
				var x = parseInt(($(this).data("gs-x")) + 1);
				var y = parseInt(($(this).data("gs-y")) + i) + 65;

				locations.push( String.fromCharCode(y) + x );
			}
		}
// 		
		
		var ship = {
			type: type,
			location: locations	}
		
		ships.push(ship);
	})
	console.log(ships);
};

function createGamesPage () {
	$("#running-games ol").empty();
	$("#leader-board").empty();

	$.get("http://localhost:8080/api/games", function (responseData) {
		console.log("api/games -> .get")
	}).done(function(responseData){
		createLeaderBoard(responseData.games);
		createListOfGames(responseData);
		showLogInLogOut(responseData.player);
		showWellcomeUser(responseData.player);
	});
}

function correctEmailFormat(email){
	var RegExp = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/

	return RegExp.test(email); 
}

function showWellcomeUser(player){
	if(player == null){
		$("#user-name").parent().toggle(false);
	}else{
		$("#user-name").parent().toggle(true);
		$("#user-name").text(player.email);
	}
};

function showLogInLogOut(player){
	if (player == null) {
		$("#btn-log-out").toggle( false );
		$("#btn-log-in").toggle( true );
	}else {
		$("#btn-log-out").toggle( true );
		$("#btn-log-in").toggle( false );	}
}

function logInUser(email, password){
	$.post("/api/login", { username: email, password: password })
		.done(function(){
			$(".modal").modal('toggle');
			createGamesPage();
		})
		.fail(function() {
			$("#emailHelp").removeClass("text-muted");
			$("#emailHelp").addClass("text-danger");
			$("#emailHelp").text("Please check the User and Password");
		})
}

function SignUpUser(email, password){
	$.post("/api/players", { username: email, password: password })
		.done(function(){
			logInUser(email, password);
		})
		.fail(function(data) {
			$("#emailHelp").removeClass("text-muted");
			$("#emailHelp").addClass("text-danger");
			$("#emailHelp").text(data.responseText);
		})
}
function logOutUser(){
	$.post("/api/logout")
		.done(function(){
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

function placeSalvos(salvoes) {
	$.each(salvoes, function (index, salvo) {
		if (salvo.player == playerOne.id) {
			placeSalvo($("#salvoes-grid"), salvo.turn, salvo.locations)
		} else {
			placeSalvo($("#game-grid"), salvo.turn, salvo.locations)
		}
	})
}

function placeSalvo(el_grid, turn, locations) {
	$.each(locations, function (index, location) {
		el_salvo = el_grid.find("." + location).text(turn);
		if (el_salvo.data().shipOnCell == true) {
			el_salvo.addClass("hit");
		} else {
			el_salvo.addClass("salvo");
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
	$.each(ships, function (index, ship) {
		$.each(ship.locations, function (index, location) {
			$("#game-grid").find("." + location).addClass("ship").data("shipOnCell", true);
		});
	});
}

function createGameGrids() {
//	createGrid($("#game-grid"));
	createGrid($("#salvoes-grid"))
}

function createGrid(el_grid) {

	// Filas
	for (let i = 64; i < 76; i++) {
		el_li_tr = $(document.createElement("tr"));
		el_grid.append(el_li_tr);
		for (let j = 0; j < 11; j++) {
			el_li_td = $(document.createElement("td")).addClass("content");
			if (i == 64 && j == 0) {
				el_li_tr.append(el_li_td.text(""));
			} else if (i == 64) {
				el_li_tr.append(el_li_td.text(j));
			} else if (j == 0) {
				el_li_tr.append(el_li_td.text(String.fromCharCode(i)));
			} else {
				el_li_tr.append(el_li_td.text(""));
				el_li_td.attr("class", String.fromCharCode(i) + j);
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

				if(loggedPlayer){
					
					el_btn_enter_game.attr("href","/web/game.html?gp="+ loggedPlayer.id);
					el_btn_game = el_btn_enter_game;
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