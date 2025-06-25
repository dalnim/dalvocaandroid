var writer;
var isOutlineVisible;
var opts = {
    showOutline: isOutlineVisible,
    onCorrectStroke: function(strokeData) {
        const quizOnCorrectStroke = {"quizOnCorrectStroke": strokeData};
        window.AndroidInterface.onCorrectStroke(JSON.stringify(quizOnCorrectStroke));
    },
    onComplete: function(summaryData) {
        const quizOnComplete = {"quizOnComplete": summaryData};
        window.AndroidInterface.onQuizCompleted(JSON.stringify(quizOnComplete));
    }
};

function updateCharacter(character, showOutline, boxWidth, boxHeight) {
    if (!character || !character.length) {
        return;
    }
	document.querySelector('#target').innerHTML = '';
	window.location.hash = character;
    isOutlineVisible = showOutline;
    writer = HanziWriter.create('target', character, {
        width: boxWidth,
        height: boxHeight,
        renderer: 'canvas',
        radicalColor: '#166E16',
        strokeHighlightSpeed: 1.5,
        highlightColor: '#F00',
        drawingColor: '#333',
        strokeColor: '#555',
        outlineColor: '#DDD',
        drawingWidth: 40,
        showHintAfterMisses: 1,
        strokeAnimationSpeed: 2.0,
        delayBetweenStrokes: 100,
        highlightOnComplete: false,

//        radicalColor: '#166E16',
//        strokeHighlightSpeed: 0.6,
//        highlightColor: '#F00',
//        showHintAfterMisses: 1,
//        strokeAnimationSpeed: 2,
//        delayBetweenStrokes: 100,
//        highlightOnComplete: true,
        onLoadCharDataError: function(reason) {
            window.AndroidInterface.onLoadCharDataError();
        }
    });
    window.writer = writer;
    refreshQuiz();
}

function refreshQuiz(showOutline) {
    writer.quiz(opts);
    showWriterQuiz(showOutline);
}

function showWriterQuiz(showOutline) {
    if (showOutline) {
        writer.showOutline();
    } else {
        writer.hideOutline();
    }
}

function animateCharacter() {
    writer.animateCharacter({
        onComplete: function() {
            window.AndroidInterface.onAnimateCharacterCompleted();
        }
    });
}

function highlightStroke(strokeNum) {
    writer.highlightStroke(strokeNum, {
        onComplete: function() {
            window.AndroidInterface.onHighlightStrokeCompleted(strokeNum);
        }
    });
}

window.onload = function() {
    window.AndroidInterface.onHtmlLoaded();
}
