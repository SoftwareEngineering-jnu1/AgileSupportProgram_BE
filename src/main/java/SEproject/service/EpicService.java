package SEproject.service;

import SEproject.domain.Epic;
import SEproject.dto.NewEpicDTO;
import SEproject.repository.EpicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EpicService {
    private final EpicRepository epicRepository;

    @Autowired
    public EpicService(EpicRepository epicRepository) {
        this.epicRepository = epicRepository;
    }

    public Epic createEpic(NewEpicDTO newEpicDTO, Long projectId) {
        return epicRepository.save(newEpicDTO, projectId);
    }
}
