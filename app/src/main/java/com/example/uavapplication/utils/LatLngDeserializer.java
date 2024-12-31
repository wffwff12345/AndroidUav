package com.example.uavapplication.utils;

import com.fasterxml.jackson.core.JsonParser;
   import com.fasterxml.jackson.databind.DeserializationContext;
   import com.fasterxml.jackson.databind.JsonDeserializer;
   import com.fasterxml.jackson.databind.JsonNode;
   import com.baidu.mapapi.model.LatLng;

   import java.io.IOException;

   public class LatLngDeserializer extends JsonDeserializer<LatLng> {
       @Override
       public LatLng deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
           JsonNode node = p.getCodec().readTree(p);
           double latitude = node.get("latitude").asDouble();
           double longitude = node.get("longitude").asDouble();
           return new LatLng(latitude, longitude);
       }
   }
   