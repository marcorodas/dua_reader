package pe.as.support.entities;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Response {
    private final Dua dua;
    private final List<Mercancia> mercancias = new ArrayList<>();
}
