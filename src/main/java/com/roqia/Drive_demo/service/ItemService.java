package com.roqia.Drive_demo.service;

import com.roqia.Drive_demo.error.customExceptions.RecordNotFoundException;
import com.roqia.Drive_demo.model.File;
import com.roqia.Drive_demo.model.Folder;
import com.roqia.Drive_demo.model.Item;
import com.roqia.Drive_demo.repo.ItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class ItemService {
    @Autowired
     private ItemRepo itemRepo;


    public Item getItem(int user_id,int item_id){
        Optional<Item> item  = itemRepo.findByIdAndOwner_Id(item_id,user_id);
        if(item.isPresent()){
            return item.get();
        }
        else {
            throw new RecordNotFoundException("No such item found with id :"+item_id);
        }
    }
    public String get_item_path(Item item){
        List<String> path = new ArrayList<>();
        String name="";
        if (item instanceof File file) {
            name = file.getName()+"_"+file.getVersion()+"."+file.getFileExtension();
            path.add(name);
            Folder parent = file.getFolder();
            while (parent!=null){
                path.add(parent.getName());
                parent=parent.getParentFolder();

            }
        } else if (item instanceof Folder folder) {
            path.add(folder.getName());
            Folder parent =folder.getParentFolder();
            while (parent != null) {
                path.add(parent.getName());
                parent = parent.getParentFolder();
            }
        }
        else {
            return "";
        }
        Collections.reverse(path);
        return "storage/" + String.join("/", path);
    }

    public List<String> searchByItemName(int userId,String itemName){
       List<Item> items = itemRepo.searchByName(userId,itemName);
      List<String>itemNames = new ArrayList<>();
      for (Item item :items){
          itemNames.add(item.getName());
      }
      return itemNames;
    }
}
