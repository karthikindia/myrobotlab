/**
 *                    
 * @author Mats (at) myrobotlab.org
 *  
 * This file is part of MyRobotLab (http://myrobotlab.org).
 *
 * MyRobotLab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version (subject to the "Classpath" exception
 * as provided in the LICENSE.txt file that accompanied this code).
 *
 * MyRobotLab is distributed in the hope that it will be useful or fun,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * All libraries in thirdParty bundle are subject to their own license
 * requirements - please refer to http://myrobotlab.org/libraries for 
 * details.
 * 
 * Enjoy !
 * 
 * */

package org.myrobotlab.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.service.I2cMux;
import org.myrobotlab.service.Runtime;
import org.myrobotlab.service.SwingGui;
import org.slf4j.Logger;

public class I2cMuxGui extends ServiceGui implements ActionListener {

  static final long serialVersionUID = 1L;
  public final static Logger log = LoggerFactory.getLogger(I2cMuxGui.class);

  String attach = "attach";
  String detach = "detach";
  JButton attachButton = new JButton(attach);

  JComboBox<String> controllerList = new JComboBox<String>();
  JComboBox<String> deviceAddressList = new JComboBox<String>();
  JComboBox<String> deviceBusList = new JComboBox<String>();

  JLabel controllerLabel = new JLabel("Controller");
  JLabel deviceBusLabel = new JLabel("Bus");
  JLabel deviceAddressLabel = new JLabel("Address");

  I2cMux boundService = null;

  public I2cMuxGui(final String boundServiceName, final SwingGui myService) {
    super(boundServiceName, myService);
    boundService = (I2cMux) Runtime.getService(boundServiceName);

    addTopLine(createFlowPanel("Controller", attachButton, "Controller", controllerList, "Bus", deviceBusList, "Address", deviceAddressList));

    refreshControllers();
    getDeviceBusList();
    getDeviceAddressList();
    restoreListeners();
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    Object o = event.getSource();
    if (o == attachButton) {
      if (attachButton.getText().equals(attach)) {
        int index = controllerList.getSelectedIndex();
        if (index != -1) {
          swingGui.send(boundServiceName, attach, controllerList.getSelectedItem(), deviceBusList.getSelectedItem(), deviceAddressList.getSelectedItem());
        }
      } else {
        log.info("detach {}", controllerList.getSelectedItem());
        swingGui.send(boundServiceName, detach, controllerList.getSelectedItem());
      }
    }
  }

  @Override
  public void subscribeGui() {
  }

  @Override
  public void unsubscribeGui() {
  }

  public void onState(I2cMux service) {

    removeListeners();
    refreshControllers();
    if (service.controller != null) {
      controllerList.setSelectedItem(service.controllerName);
      deviceBusList.setSelectedItem(service.deviceBus);
      deviceAddressList.setSelectedItem(service.deviceAddress);
    }
    if (service.isAttached) {
      attachButton.setText(detach);
      controllerList.setEnabled(false);
      deviceBusList.setEnabled(false);
      deviceAddressList.setEnabled(false);
    } else {
      attachButton.setText(attach);
      controllerList.setEnabled(true);
      deviceBusList.setEnabled(true);
      deviceAddressList.setEnabled(true);
    }
    restoreListeners();
  }

  public void getDeviceBusList() {
    List<String> mbl = boundService.deviceBusList;
    for (int i = 0; i < mbl.size(); i++) {
      deviceBusList.addItem(mbl.get(i));
    }
  }

  public void getDeviceAddressList() {

    List<String> mal = boundService.deviceAddressList;
    for (int i = 0; i < mal.size(); i++) {
      deviceAddressList.addItem(mal.get(i));
    }
  }

  public void refreshControllers() {
    List<String> v = boundService.refreshControllers();
    controllerList.removeAllItems();
    for (int i = 0; i < v.size(); ++i) {
      controllerList.addItem(v.get(i));
    }
    if (boundService.controller != null) {
      controllerList.setSelectedItem(boundService.controller.getName());
    }
  }

  public void removeListeners() {
    attachButton.removeActionListener(this);
  }

  public void restoreListeners() {
    attachButton.addActionListener(this);
  }

}
