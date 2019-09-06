//
//  ViewController.swift
//  QRScan
//
//  Created by Nimble Chapps on 8/10/17.
//  Copyright © 2017 nimblechapps. All rights reserved.
//

import UIKit
import AVFoundation

class ViewController: UIViewController, AVCaptureMetadataOutputObjectsDelegate {
    @IBOutlet weak var viewPreview: UIView!
    @IBOutlet weak var lblString: UILabel!
    @IBOutlet weak var btnStartStop: UIButton!
    var captureSession: AVCaptureSession?
    var videoPreviewLayer: AVCaptureVideoPreviewLayer!
    var isReading: Bool = false
    
    @IBOutlet weak var msisdn: UITextField!
    
    // MARK: - View Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        viewPreview.layer.cornerRadius = 5;
        btnStartStop.layer.cornerRadius = 5;
        captureSession = nil;
        lblString.text = "QR code discription...";
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - IBAction Method
    
    @IBAction func startStopClick(_ sender: UIButton) {
        if !isReading {
            if (self.startReading()) {
            btnStartStop.setTitle("Stop", for: .normal)
            lblString.text = "Scanning for QR Code..."
            }
        }
        else {
            stopReading()
            btnStartStop.setTitle("Start", for: .normal)
        }
        isReading = !isReading
    }
    
    // MARK: - Custom Method
    func startReading() -> Bool {
        let captureDevice = AVCaptureDevice.defaultDevice(withMediaType: AVMediaTypeVideo)
        do {
            let input = try AVCaptureDeviceInput(device: captureDevice)
            captureSession = AVCaptureSession()
            captureSession?.addInput(input)
            // Do the rest of your work...
        } catch let error as NSError {
            // Handle any errors
            print(error)
            return false
        }
        
        videoPreviewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
        videoPreviewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
        videoPreviewLayer.frame = viewPreview.layer.bounds
        viewPreview.layer.addSublayer(videoPreviewLayer)
        
        let captureMetadataOutput = AVCaptureMetadataOutput()
        captureSession?.addOutput(captureMetadataOutput)
        captureMetadataOutput.metadataObjectTypes = captureMetadataOutput.availableMetadataObjectTypes
        print(captureMetadataOutput.availableMetadataObjectTypes)
        captureMetadataOutput.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
        captureSession?.startRunning()
        
        return true
    }
    func stopReading() {
        captureSession?.stopRunning()
        captureSession = nil
        videoPreviewLayer.removeFromSuperlayer()
    }
    
    func captureOutput(_ captureOutput: AVCaptureOutput!, didOutputMetadataObjects metadataObjects: [Any]!, from connection: AVCaptureConnection!) {
        for data in metadataObjects {
            let metaData = data as! AVMetadataObject
            print(metaData.description)
            let transformed = videoPreviewLayer?.transformedMetadataObject(for: metaData) as? AVMetadataMachineReadableCodeObject
            if let unwraped = transformed {
                print(unwraped.stringValue)
                lblString.text = unwraped.stringValue
                self.qrVerification(payload: unwraped.stringValue)
                btnStartStop.setTitle("Start", for: .normal)
                self.performSelector(onMainThread: #selector(stopReading), with: nil, waitUntilDone: false)
                isReading = false;
            }
        }
    }
    
    func qrVerification(payload:String){
        
        if self.msisdn.text == "" {
            self.showToast(message: "Enter Mobile Number")
            return
        }
        
        let deviceID = UIDevice.current.identifierForVendor!.uuidString
        let json = [
                    "qrValue" : payload,
                    "msisdn" : self.msisdn.text!,
                    "imei" : deviceID
                   ] as [String : Any]
        print(json)
        var jsonData = Data()
        do {
            jsonData = try JSONSerialization.data(withJSONObject: json, options: .prettyPrinted)
        }
        catch {
            print(error.localizedDescription)
        }
        let url = NSURL(string: "http://34.251.96.227/api/qrVerification")!
        print(url)
        var request = URLRequest(url: url as URL)
        request.httpMethod = "POST"
        request.addValue("application/json;charset=UTF-8", forHTTPHeaderField: "content-type")
        request.httpBody = jsonData
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        let task = session.dataTask(with: request, completionHandler: {(data, response, error) in
            do {
                DispatchQueue.global(qos: .userInitiated).sync {
                    DispatchQueue.main.async {
                        let otpSent = OTPViewController()
                        self.navigationController?.pushViewController(otpSent, animated: true)
                    }
                }
            }
        });
        task.resume()
    }
}

