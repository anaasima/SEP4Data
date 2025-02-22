package via.sep4gr2.sep4websocketstest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import via.sep4gr2.sep4websocketstest.models.database.DimPlant;
import via.sep4gr2.sep4websocketstest.models.databaseEDW.EDWDimPlant;
import via.sep4gr2.sep4websocketstest.services.PlantService;

import java.util.List;

@RestController
@RequestMapping("/plants")
public class PlantController
{
    @Autowired
    private PlantService plantService;

    @CrossOrigin(origins = "*")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    void insertPlant(@RequestBody DimPlant plant)
    {
        try
        {
            plantService.insertPlant(plant);
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping
    public @ResponseBody
    List<EDWDimPlant> getAllPlants(){
        return plantService.getAllPlants();
    }
}
