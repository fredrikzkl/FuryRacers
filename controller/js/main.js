// global Phaser

var game;

function setBackgroundColor(color){

  game.stage.backgroundColor = color;
}

(function (Phaser) {
  'use strict';

  var isLeftUp = false;
  var isLeftDown = false;
  var isRightUp = false;
  var isRightDown = false;
  var isThrottleUp = false;
  var isThrottleDown = false;

  var throttleId = 1;
  var rightArrowId = 2;
  var leftArrowId = 3;

  var arrowImageScale = 0.25;
  var originalArrowImageWidth = 200;
  var originalArrowImageHeight = 250;
  var arrowSpriteWidth = originalArrowImageWidth * arrowImageScale;
  var arrowSpriteHeight = originalArrowImageHeight * arrowImageScale;

  var arrowLeftArea;
  var arrowRightArea;
  var arrowsBackground;
  var redButtons;
  var tools_button;

  var lightGray = "0xb3b3b3"
  var offWHITE = '0xEFEFEF';

  var buttonDown = function(buttonID){};
  var buttonUp = function(buttonID){};

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

  function createThrottleButton(){

    redButtons = game.add.sprite(game.width/1.4, game.height/4, 'redButtons');
    redButtons.frame = 0;
    redButtons.scale.setTo(1.5,1.8);
    redButtons.inputEnabled = true;
  }

  function createArrowKeys(){

    var arrowsBackgroundX = 0;
    var arrowsBackgroundY = 0;
    var arrowsBackgroundWidth = game.stage.width / 2.5;
    var arrowsBackgroundHeight = game.stage.height;

    var margin = arrowsBackgroundHeight / 200;

    var arrowLeftAreaX = arrowsBackgroundX + margin;
    var arrowLeftAreaY = arrowsBackgroundY + margin;
    var amountOfMarginsWidth = 3;
    var arrowLeftAreaWidth = (arrowsBackgroundWidth - amountOfMarginsWidth * margin) / 2;
    var arrowLeftAreaHeight = (arrowsBackgroundHeight - 2 * margin);
    var arrowLeftAreaEndX = arrowLeftAreaX + arrowLeftAreaWidth;
    var arrowLeftAreaEndY = arrowLeftAreaY + arrowLeftAreaHeight;

    var arrowRightAreaX = arrowLeftAreaEndX + margin;
    var arrowRightAreaY = arrowLeftAreaY;
    var arrowRightAreaWidth = arrowLeftAreaWidth;
    var arrowRightAreaHeight = arrowLeftAreaHeight;
    var arrowRightAreaEndX = arrowRightAreaX + arrowRightAreaWidth;
    var arrowRightAreaEndY = arrowRightAreaY + arrowRightAreaHeight; 

    var middleOfLeftArrowAreaX = (arrowLeftAreaX + arrowLeftAreaEndX) / 2;
    var middleOfLeftArrowAreaY = (arrowLeftAreaY + arrowLeftAreaEndY) / 2;

    var middleOfRightArrowAreaX = (arrowRightAreaX + arrowRightAreaEndX) / 2;
    var middleOfRightArrowAreaY = (arrowRightAreaY + arrowRightAreaEndY) / 2;

    drawLeftArrowArea(arrowLeftAreaX, arrowLeftAreaY, arrowLeftAreaWidth, arrowLeftAreaHeight);
    drawRightArrowArea(arrowRightAreaX, arrowRightAreaY, arrowRightAreaWidth, arrowRightAreaHeight);

    addLeftArrowImage(middleOfLeftArrowAreaX, middleOfLeftArrowAreaY);
    addRightArrowImage(middleOfRightArrowAreaX, middleOfRightArrowAreaY);

  }

  function drawLeftArrowArea(arrowLeftAreaX, arrowLeftAreaY, arrowLeftAreaWidth, arrowLeftAreaHeight) {

    arrowLeftArea = game.add.graphics(0,0);
    arrowLeftArea.beginFill(lightGray,1);
    arrowLeftArea.drawRect(
      arrowLeftAreaX,
      arrowLeftAreaY,
      arrowLeftAreaWidth,
      arrowLeftAreaHeight);
    arrowLeftArea.inputEnabled = true;
  }

  function drawRightArrowArea(arrowRightAreaX, arrowRightAreaY, arrowRightAreaWidth, arrowRightAreaHeight) {

    arrowRightArea = game.add.graphics(0,0);
    arrowRightArea.beginFill(lightGray,1);
    arrowRightArea.drawRect(
      arrowRightAreaX,
      arrowRightAreaY,
      arrowRightAreaWidth,
      arrowRightAreaHeight);
    arrowRightArea.inputEnabled = true;
  }

   function addLeftArrowImage(middleOfLeftArrowAreaX, middleOfLeftArrowAreaY){

    var leftArrowImage;
    leftArrowImage = game.add.sprite(middleOfLeftArrowAreaX - arrowSpriteWidth, 
                                    middleOfLeftArrowAreaY - arrowSpriteHeight, 'arrows');
    leftArrowImage.frame = 0;
    leftArrowImage.scale.setTo(0.5, 0.5);
  }

  function addRightArrowImage(middleOfRightArrowAreaX, middleOfRightArrowAreaY){

    var rightArrowImage;
    rightArrowImage = game.add.sprite(middleOfRightArrowAreaX - arrowSpriteWidth, 
                                      middleOfRightArrowAreaY - arrowSpriteHeight, 'arrows');
    rightArrowImage.frame = 1;
    rightArrowImage.scale.setTo(0.5, 0.5);
  }

  function createToolsButton(){

    var screenWidth = game.width;
    var screenHeight = game.height;
    var toolsButtonY = screenHeight / 90; 
    var toolsButtonX = screenWidth - screenWidth / 15;

    tools_button = game.add.sprite(toolsButtonX, toolsButtonY, 'tools_button');
    tools_button.scale.setTo(0.02,0.025);
    tools_button.inputEnabled = true;
  }

  function createButtonEvents(game){

    tools_button.events.onInputDown.add(usernamePrompt, game);

    arrowLeftArea.events.onInputOver.add(leftDown, game);
    arrowLeftArea.events.onInputDown.add(leftDown, game);

    arrowRightArea.events.onInputOver.add(rightDown, game);
    arrowRightArea.events.onInputDown.add(rightDown, game);

    arrowLeftArea.events.onInputOut.add(leftUp, game);
    arrowLeftArea.events.onInputUp.add(leftUp, game);

    arrowRightArea.events.onInputOut.add(rightUp, game);
    arrowRightArea.events.onInputUp.add(rightUp, game);

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
    },

    create: function create(){

      game.scale.scaleMode = Phaser.ScaleManager.EXACT_FIT;

      createToolsButton();
      createThrottleButton();
      createArrowKeys();
      createButtonEvents(this);

      setBackgroundColor(offWHITE);
    }
  });

}.call(this, Phaser));
