package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    public List<LabelDTO> index() {
        var labels = labelRepository.findAll();

        return labels.stream()
                .map(l -> labelMapper.map(l))
                .toList();
    }

    public LabelDTO show(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Label with id " + id + " not found"
                ));
        return labelMapper.map(label);
    }

    public LabelDTO create(LabelCreateDTO dto) {
        var label = labelMapper.map(dto);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public LabelDTO update(Long id, LabelUpdateDTO dto) {
         var label = labelRepository.findById(id)
                 .orElseThrow(() -> new ResourceNotFoundException(
                         "Label with id " + id + " not found"
                 ));
         labelMapper.update(label, dto);
         labelRepository.save(label);
         return labelMapper.map(label);
    }

    public void delete(Long id) {
        labelRepository.deleteById(id);
    }

}
