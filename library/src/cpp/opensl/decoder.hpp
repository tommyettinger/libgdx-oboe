#pragma once
#include <memory>
#include <vector>

#include "context.hpp"
#include "buffer_player.hpp"

namespace opensl {
    class decoder {
        public:
            decoder(const context& p_context);
            decoder(decoder&& p_decoder);

            void open(int p_file_descriptor, off_t p_start, off_t p_length);
            std::vector<int16_t> request_more(int p_samples);

            bool is_opened();

            void position(float p_position);
            float position();
            float content_duration();

            static std::vector<int16_t> decode_full(const context& p_context,
                                                    int p_file_descriptor,
                                                    off_t p_start,
                                                    off_t p_length);
        private:
            void reattach_callback();

            const context& m_context;
            std::unique_ptr<buffer_player> m_player;
            buffer_player::pcm_container m_merged_buffers;
    };
}