var exec = require('cordova/exec');

exports.coolMethod = function (success, error) {
    exec(success, error, 'MediaCapturePlus', 'coolMethod');
};

exports.testOpenCamera = function (success, error) {
    exec(success, error, 'MediaCapturePlus', 'testOpenCamera');
};

exports.openImageAssessment = function (success, error) {
    exec(success, error, 'MediaCapturePlus', 'openImageAssessment');
};

exports.createCaptureServive = function (success, error) {
    exec(success, error, 'MediaCapturePlus', 'createCaptureServive');
};

exports.startCaptureService = function (success, error) {
    exec(success, error, 'MediaCapturePlus', 'startCaptureService');
};

exports.stopCaptureService = function (success, error) {
    exec(success, error, 'MediaCapturePlus', 'stopCaptureService');
};