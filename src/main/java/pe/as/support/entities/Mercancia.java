package pe.as.support.entities;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Mercancia {
    private final int serie;
    private final List<String> detalles = new ArrayList<>();
}
