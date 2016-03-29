/* global Phaser */

function setBackgroundColor(color){

  game.stage.backgroundColor = color;
}

var game;

(function (Phaser) {
  'use strict';

  var isLeftDown = false;
  var isRightDown = false;
  var isLeftUp = false;
  var isRightUp = false;
  var isThrottleDown = false;
  var isThrottleUp = false;

  var throttleId = 1;
  var rightArrowId = 2;
  var leftArrowId = 3;

  var arrowLeftArea;
  var arrowRightArea;
  var backgroundarrowLeftArea;
  var bakcgroundArrowRightArea;
  var whitePaddleBackground;
  var redButtons;
  var tools_button;

  var GREY = "0xbebbbd";
  var lightGray = "0xb3b3b3"
  var BLACK = "0x111213";
  var WHITE = "0xffffff";
  var RED = "0xD40100";
  var offWHITE = '0xEFEFEF';
  var greenYellowish = "  0xb8b894";

  var buttonDown = function(buttonID){};
  var buttonUp = function(buttonID){};

  var throttle = function(){console.log("Throttle")};

  var usernamePrompt = function(){  

    var newUsername = prompt("Enter your name! (q to quit)", username);

    if (newUsername == "" || newUsername == username || newUsername == null) {
        return;
    }else if(newUsername == "q"){
        sendToBackend("disconnect");
        return;
    }

    setUsername(newUsername);
    sendToBackend("set username", username);
  };

  var leftDown = function() { 
    if(!isLeftDown){ 
      isLeftDown = true; 
      isLeftUp = false; 
      buttonDown(leftArrowId);
    }
  };

  var rightDown = function(){ 
    if(!isRightDown){ 
      isRightDown = true; 
      isRightUp = false;
      buttonDown(rightArrowId);
    }
  };

  var leftUp = function(){ 
    if(!isLeftUp){ 
      isLeftUp = true; 
      isLeftDown = false; 
      buttonUp(leftArrowId);
    }
  };
  
  var rightUp = function(){ 
    if(!isRightUp){ 
      isRightUp = true; 
      isRightDown = false; 
      buttonUp(rightArrowId);
    }
  };

  var throttleDown = function(){ 
    if(!isThrottleDown){ 
      isThrottleDown = true; 
      isThrottleUp = false;
      buttonDown(throttleId);
      redButtons.frame = 1;
    }
  };

  var throttleUp = function(){ 
    if(!isThrottleUp){ 
      isThrottleUp = true; 
      isThrottleDown = false;
      buttonUp(throttleId);
      redButtons.frame = 0;
    }
  };

  function createThrottleButton(dis){

    redButtons = game.add.sprite(dis.game.width/1.4, dis.game.height/4, 'redButtons');
    redButtons.data = 1;
    redButtons.frame = 0;
    redButtons.scale.setTo(1.5,1.8);
    redButtons.inputEnabled = true;
  }

  function createToolsButton(dis){

    var screenWidth = dis.game.width;
    var screenHeight = dis.game.height;
    var toolsButtonY = screenHeight / 90; 
    var toolsButtonX = screenWidth - screenWidth/15;

    tools_button = game.add.sprite(toolsButtonX, toolsButtonY, 'tools_button');
    tools_button.data = 4;
    tools_button.scale.setTo(0.02,0.025);
    tools_button.inputEnabled = true;
  }

  function createButtonEvents(game){

    tools_button.events.onInputDown.add(usernamePrompt, game);

    backgroundarrowLeftArea.events.onInputOver.add(leftDown, game);
    backgroundarrowLeftArea.events.onInputDown.add(leftDown, game);

    bakcgroundArrowRightArea.events.onInputOver.add(rightDown, game);
    bakcgroundArrowRightArea.events.onInputDown.add(rightDown, game);

    backgroundarrowLeftArea.events.onInputOut.add(leftUp, game);
    backgroundarrowLeftArea.events.onInputUp.add(leftUp, game);

    bakcgroundArrowRightArea.events.onInputOut.add(rightUp, game);
    bakcgroundArrowRightArea.events.onInputUp.add(rightUp, game);

    redButtons.events.onInputDown.add(throttleDown, game);
    redButtons.events.onInputOver.add(throttleDown, game);

    redButtons.events.onInputOut.add(throttleUp, game);
    redButtons.events.onInputUp.add(throttleUp, game);
  }

    game = new Phaser.Game(800, 600, Phaser.AUTO, 'controller', {

    preload: function preload() {

      addMessageHandler(function(msg){
        if(msg == "identified"){

          buttonDown = function(buttonID) {
            sendToGame("buttonDown", buttonID);
          }

          buttonUp = function(buttonID){
            sendToGame("buttonUp", buttonID);
          }
        }
    });

  
    game.load.image('tools_button', 'assets/Tools_button.png');
    game.load.spritesheet('redButtons', 'assets/redButtons.png', 150, 149);
    game.load.spritesheet('arrows', 'assets/arrows-2x.png', 200, 250);
    //game.load.audio('directionChange', 'assets/squit.ogg');

    },

    create: function create(){

      setBackgroundColor(offWHITE, game);

      game.scale.scaleMode = Phaser.ScaleManager.EXACT_FIT;
      game.scale.fullScreenScaleMode = Phaser.ScaleManager.EXACT_FIT;
      game.scale.startFullScreen(false);
      game.scale.refresh();

      createToolsButton(this);
      createThrottleButton(this);
     
      var whitePaddleBackgroundX = 0;
      var whitePaddleBackgroundY = 0;
      var whitePaddleBackgroundWidth = game.stage.width/2.5;
      var whitePaddleBackgroundHeight = game.stage.height;

      var arrowKeysMargin = whitePaddleBackgroundHeight/200;

      var arrowLeftAreaX = whitePaddleBackgroundX + arrowKeysMargin;
      var arrowLeftAreaY = whitePaddleBackgroundY + arrowKeysMargin;
      var arrowLeftAreaWidth = (whitePaddleBackgroundWidth - 3*arrowKeysMargin)/2;
      var arrowLeftAreaHeight = (whitePaddleBackgroundHeight - 2*arrowKeysMargin);
      var arrowLeftAreaEndX = arrowLeftAreaX + arrowLeftAreaWidth;
      var arrowLeftAreaEndY = arrowLeftAreaY + arrowLeftAreaHeight;

      var arrowRightAreaX = arrowLeftAreaEndX + arrowKeysMargin;
      var arrowRightAreaY = arrowLeftAreaY;
      var arrowRightAreaWidth = arrowLeftAreaWidth;
      var arrowRightAreaHeight = arrowLeftAreaHeight;
      var arrowRightAreaEndX = arrowRightAreaX + arrowRightAreaWidth;
      var arrowRightAreaEndY = arrowRightAreaY + arrowRightAreaHeight; 

      var middleOfarrowLeftAreaX = (arrowLeftAreaX + arrowLeftAreaEndX)/2;
      var middleOfarrowLeftAreaY = (arrowLeftAreaY + arrowLeftAreaEndY)/2;

      var middleOfArrowRightAreaX = (arrowRightAreaX + arrowRightAreaEndX)/2;
      var middleOfArrowRightAreaY = (arrowRightAreaY + arrowRightAreaEndY)/2;

     
    //Pilene som skal ligge over det hvite området
      arrowLeftArea = game.add.graphics(0,0);
      arrowLeftArea.beginFill(lightGray,1);
      arrowLeftArea.drawRect(
        arrowLeftAreaX,
        arrowLeftAreaY,
        arrowLeftAreaWidth,
        arrowLeftAreaHeight);

      arrowRightArea = game.add.graphics(0,0);
      arrowRightArea.beginFill(lightGray,1);
      arrowRightArea.drawRect(
        arrowRightAreaX,
        arrowRightAreaY,
        arrowRightAreaWidth,
        arrowRightAreaHeight);


      bakcgroundArrowRightArea = game.add.sprite(0, 0);
      bakcgroundArrowRightArea.addChild(arrowRightArea);
      bakcgroundArrowRightArea.data = 2; // knappID/buttonID
      bakcgroundArrowRightArea.inputEnabled = true;

      backgroundarrowLeftArea = game.add.sprite(0, 0);
      backgroundarrowLeftArea.addChild(arrowLeftArea);
      backgroundarrowLeftArea.data = 3; //knappID/buttonID
      backgroundarrowLeftArea.inputEnabled = true;

      var arrowImageScale = 0.25;
      var arrowSpriteWidth = 200*arrowImageScale;
      var arrowSpriteHeight = 250*arrowImageScale;

      var leftArrowImage = game.add.sprite(middleOfarrowLeftAreaX - arrowSpriteWidth, middleOfarrowLeftAreaY - arrowSpriteHeight, 'arrows');
          leftArrowImage.frame = 0;
          leftArrowImage.scale.setTo(0.5, 0.5);


      var rightArrowImage = game.add.sprite(middleOfArrowRightAreaX - arrowSpriteWidth, middleOfArrowRightAreaY - arrowSpriteHeight, 'arrows');
          rightArrowImage.frame = 1;
          rightArrowImage.scale.setTo(0.5, 0.5);

      createButtonEvents(this);
    
    }

  });

}.call(this, Phaser));
